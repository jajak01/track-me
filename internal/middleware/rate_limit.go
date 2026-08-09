package middleware

import (
	"net/http"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
	"golang.org/x/time/rate"

	"github.com/desen/track-me/internal/dto"
)

type ipLimiter struct {
	limiter  *rate.Limiter
	lastSeen time.Time
}

var (
	limiters = make(map[string]*ipLimiter)
	mu       sync.Mutex
)

func init() {
	// Periodic cleanup of idle rate limiters
	go func() {
		for {
			time.Sleep(5 * time.Minute)
			mu.Lock()
			for ip, lim := range limiters {
				if time.Since(lim.lastSeen) > 10*time.Minute {
					delete(limiters, ip)
				}
			}
			mu.Unlock()
		}
	}()
}

func RateLimit(rpm int) gin.HandlerFunc {
	return func(c *gin.Context) {
		ip := c.ClientIP()

		mu.Lock()
		lim, ok := limiters[ip]
		if !ok {
			// Limit to rpm (requests per minute) which is rpm/60 requests per second.
			// Burst limit of 10.
			r := rate.Limit(float64(rpm) / 60.0)
			lim = &ipLimiter{
				limiter:  rate.NewLimiter(r, 10),
				lastSeen: time.Now(),
			}
			limiters[ip] = lim
		} else {
			lim.lastSeen = time.Now()
		}
		mu.Unlock()

		if !lim.limiter.Allow() {
			c.AbortWithStatusJSON(http.StatusTooManyRequests, dto.APIResponse{
				Success: false,
				Message: "Too many requests. Please slow down.",
			})
			return
		}

		c.Next()
	}
}

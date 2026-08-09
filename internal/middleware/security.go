package middleware

import (
	"github.com/gin-gonic/gin"
)

// SecurityHeaders sets HTTP security headers (Helmet-like protection)
func SecurityHeaders() gin.HandlerFunc {
	return func(c *gin.Context) {
		// Prevent MIME type sniffing
		c.Header("X-Content-Type-Options", "nosniff")

		// Clickjacking protection
		c.Header("X-Frame-Options", "DENY")

		// XSS protection for older browsers
		c.Header("X-XSS-Protection", "1; mode=block")

		// Referrer policy
		c.Header("Referrer-Policy", "strict-origin-when-cross-origin")

		// Strict Transport Security (only in production)
		c.Header("Strict-Transport-Security", "max-age=31536000; includeSubDomains")

		// Permissions policy (disable features we don't use)
		c.Header("Permissions-Policy", "camera=(), microphone=(), geolocation=()")

		// Cache control for sensitive pages
		c.Header("Cache-Control", "no-store, max-age=0")

		c.Next()
	}
}

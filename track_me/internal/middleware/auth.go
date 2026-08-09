package middleware

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/pkg/jwt"
)

func Auth(jwtService *jwt.Service) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			abortWithJSON(c, http.StatusUnauthorized, "Authorization header is required")
			return
		}

		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			abortWithJSON(c, http.StatusUnauthorized, "Authorization header format must be Bearer <token>")
			return
		}

		tokenString := parts[1]
		claims, err := jwtService.ValidateAccessToken(tokenString)
		if err != nil {
			abortWithJSON(c, http.StatusUnauthorized, "Invalid or expired access token")
			return
		}

		userID, err := uuid.Parse(claims.UserID)
		if err != nil {
			abortWithJSON(c, http.StatusUnauthorized, "Invalid user ID in token")
			return
		}

		c.Set("userID", userID)
		c.Set("email", claims.Email)
		c.Next()
	}
}

func abortWithJSON(c *gin.Context, code int, message string) {
	c.AbortWithStatusJSON(code, dto.APIResponse{
		Success: false,
		Message: message,
	})
}

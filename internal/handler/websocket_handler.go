package handler

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	ws "github.com/desen/track-me/internal/websocket"
	"github.com/desen/track-me/pkg/jwt"
)

type WebSocketHandler struct {
	wsHub      *ws.Hub
	jwtService *jwt.Service
}

func NewWebSocketHandler(wsHub *ws.Hub, jwtService *jwt.Service) *WebSocketHandler {
	return &WebSocketHandler{
		wsHub:      wsHub,
		jwtService: jwtService,
	}
}

// ServeWs godoc
// @Summary Connect to WebSocket
// @Description Upgrade HTTP connection to WebSocket for real-time tracking updates. Requires a valid token query param: `token=JWT_ACCESS_TOKEN` or Bearer header
// @Tags Realtime
// @Param token query string true "Access Token"
// @Success 101 {string} string "Switching Protocols"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /ws [get]
func (h *WebSocketHandler) ServeWs(c *gin.Context) {
	token := c.Query("token")
	if token == "" {
		authHeader := c.GetHeader("Authorization")
		if authHeader != "" {
			// Accept both "Bearer <token>" and raw "<token>"
			token = authHeader
			if parts := strings.SplitN(authHeader, " ", 2); len(parts) == 2 && parts[0] == "Bearer" {
				token = parts[1]
			}
		}
	}

	if token == "" {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Access token is required as a query parameter 'token' or Bearer header",
		})
		return
	}

	claims, err := h.jwtService.ValidateAccessToken(token)
	if err != nil {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Invalid or expired access token",
		})
		return
	}

	userID, err := uuid.Parse(claims.UserID)
	if err != nil {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Invalid user ID in token",
		})
		return
	}

	ws.ServeWs(h.wsHub, c.Writer, c.Request, userID)
}

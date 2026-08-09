package handler

import (
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/service"
	ws "github.com/desen/track-me/internal/websocket"
	"github.com/desen/track-me/pkg/validator"
)

type LocationHandler struct {
	locationService service.LocationService
	friendService   service.FriendService
	wsHub           *ws.Hub
}

func NewLocationHandler(
	locationService service.LocationService,
	friendService service.FriendService,
	wsHub *ws.Hub,
) *LocationHandler {
	return &LocationHandler{
		locationService: locationService,
		friendService:   friendService,
		wsHub:           wsHub,
	}
}

// RequestSharing godoc
// @Summary Request location sharing
// @Description Send a location sharing request to a friend
// @Tags Location
// @Accept json
// @Produce json
// @Param request body dto.SharingRequest true "Sharing request details"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Request sent successfully"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/sharing/request [post]
func (h *LocationHandler) RequestSharing(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.SharingRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid request payload",
		})
		return
	}

	if err := validator.Struct(req); err != nil {
		errors := validator.FormatValidationErrors(err)
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Validation failed",
			Errors:  errors,
		})
		return
	}

	friendID, err := uuid.Parse(req.FriendID)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid friend ID format",
		})
		return
	}

	err = h.locationService.RequestSharing(c.Request.Context(), userID.(uuid.UUID), friendID)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	// Notify friend via WebSocket if online
	h.wsHub.SendToUser(friendID, "notification", map[string]string{
		"type":  "sharing_request",
		"title": "Location Sharing Request",
		"body":  "A friend requested to share locations with you.",
	})

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Location sharing request sent successfully",
	})
}

// ApproveSharing godoc
// @Summary Approve location sharing request
// @Description Approve or deny a pending location sharing request
// @Tags Location
// @Accept json
// @Produce json
// @Param request body dto.SharingApproval true "Sharing approval details"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Approved or Denied successfully"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/sharing/approve [post]
func (h *LocationHandler) ApproveSharing(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.SharingApproval
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid request payload",
		})
		return
	}

	if err := validator.Struct(req); err != nil {
		errors := validator.FormatValidationErrors(err)
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Validation failed",
			Errors:  errors,
		})
		return
	}

	notifID, err := uuid.Parse(req.NotificationID)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid notification ID format",
		})
		return
	}

	err = h.locationService.ApproveSharing(c.Request.Context(), userID.(uuid.UUID), notifID, req.Approve)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	msg := "Location sharing denied"
	if req.Approve {
		msg = "Location sharing approved"
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: msg,
	})
}

// RevokeSharing godoc
// @Summary Revoke location sharing
// @Description Stop sharing location with a friend
// @Tags Location
// @Accept json
// @Produce json
// @Param id path string true "Friend User ID"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Sharing revoked successfully"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/sharing/revoke/{id} [delete]
func (h *LocationHandler) RevokeSharing(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	friendIDStr := c.Param("id")
	friendID, err := uuid.Parse(friendIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid friend ID format",
		})
		return
	}

	err = h.locationService.RevokeSharing(c.Request.Context(), userID.(uuid.UUID), friendID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to revoke location sharing",
		})
		return
	}

	// Notify friend via WebSocket if online
	h.wsHub.SendToUser(friendID, "sharing:update", map[string]interface{}{
		"friend_id": userID.(uuid.UUID).String(),
		"status":    "revoked",
	})

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Location sharing revoked successfully",
	})
}

// UpdateLocation godoc
// @Summary Post location update
// @Description Update user's current GPS location and broadcast it to active sharing friends
// @Tags Location
// @Accept json
// @Produce json
// @Param request body dto.LocationUpdateRequest true "GPS data"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=dto.LocationResponse} "Location updated"
// @Failure 400 {object} dto.APIResponse "Validation error"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/location/update [post]
func (h *LocationHandler) UpdateLocation(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.LocationUpdateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid request payload",
		})
		return
	}

	if err := validator.Struct(req); err != nil {
		errors := validator.FormatValidationErrors(err)
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Validation failed",
			Errors:  errors,
		})
		return
	}

	userUUID := userID.(uuid.UUID)
	locResp, err := h.locationService.UpdateLocation(c.Request.Context(), userUUID, req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to save location",
		})
		return
	}

	// Broadcast in real-time to active sharing friends who are connected
	go h.broadcastLocationToFriends(userUUID, locResp)

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Location updated successfully",
		Data:    locResp,
	})
}

func (h *LocationHandler) broadcastLocationToFriends(userID uuid.UUID, locResp *dto.LocationResponse) {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	friends, err := h.friendService.GetFriends(ctx, userID)
	if err != nil {
		return
	}

	for _, f := range friends {
		friendUUID, err := uuid.Parse(f.ID)
		if err != nil {
			continue
		}

		// Only broadcast if mutual location sharing is active
		active, err := h.locationService.IsSharingActive(ctx, userID, friendUUID)
		if err == nil && active {
			h.wsHub.SendToUser(friendUUID, "location:update", locResp)
		}
	}
}

// GetCurrentLocation godoc
// @Summary Get friend's current location
// @Description Retrieve the latest GPS location of a friend if mutual sharing is active
// @Tags Location
// @Accept json
// @Produce json
// @Param userId path string true "Friend User ID"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=dto.LocationResponse} "Success"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized or sharing not enabled"
// @Router /api/v1/location/current/{userId} [get]
func (h *LocationHandler) GetCurrentLocation(c *gin.Context) {
	viewerID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	ownerIDStr := c.Param("userId")
	ownerID, err := uuid.Parse(ownerIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid user ID format",
		})
		return
	}

	loc, err := h.locationService.GetCurrentLocation(c.Request.Context(), viewerID.(uuid.UUID), ownerID)
	if err != nil {
		if err == service.ErrNotFriends || err == service.ErrSharingNotEnabled {
			c.JSON(http.StatusForbidden, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Current location retrieved successfully",
		Data:    loc,
	})
}

// GetLocationHistory godoc
// @Summary Get friend's location history
// @Description Retrieve location history of a friend if mutual sharing is active
// @Tags Location
// @Accept json
// @Produce json
// @Param userId path string true "Friend User ID"
// @Param start query string true "Start timestamp in ISO 8601 / RFC 3339 format"
// @Param end query string true "End timestamp in ISO 8601 / RFC 3339 format"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=[]dto.LocationResponse} "Success"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized or sharing not enabled"
// @Router /api/v1/location/history/{userId} [get]
func (h *LocationHandler) GetLocationHistory(c *gin.Context) {
	viewerID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	ownerIDStr := c.Param("userId")
	ownerID, err := uuid.Parse(ownerIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid user ID format",
		})
		return
	}

	startStr := c.Query("start")
	endStr := c.Query("end")

	if startStr == "" || endStr == "" {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Start and end query parameters are required",
		})
		return
	}

	start, err := time.Parse(time.RFC3339, startStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid start timestamp format. Use RFC 3339 (e.g. 2026-08-06T06:57:55Z)",
		})
		return
	}

	end, err := time.Parse(time.RFC3339, endStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid end timestamp format. Use RFC 3339 (e.g. 2026-08-06T06:57:55Z)",
		})
		return
	}

	locs, err := h.locationService.GetLocationHistory(c.Request.Context(), viewerID.(uuid.UUID), ownerID, start, end)
	if err != nil {
		if err == service.ErrNotFriends || err == service.ErrSharingNotEnabled {
			c.JSON(http.StatusForbidden, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Location history retrieved successfully",
		Data:    locs,
	})
}

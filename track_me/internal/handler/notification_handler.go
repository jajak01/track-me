package handler

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/service"
)

type NotificationHandler struct {
	notifService service.NotificationService
}

func NewNotificationHandler(notifService service.NotificationService) *NotificationHandler {
	return &NotificationHandler{notifService: notifService}
}

// GetNotifications godoc
// @Summary List notifications
// @Description Retrieve a paginated list of notifications for the authenticated user
// @Tags Notification
// @Accept json
// @Produce json
// @Param limit query int false "Number of records to return (default 20)"
// @Param page query int false "Page number (default 1)"
// @Security BearerAuth
// @Success 200 {object} dto.PaginatedResponse{data=[]dto.NotificationResponse} "Success"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/notification/list [get]
func (h *NotificationHandler) GetNotifications(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	limitStr := c.DefaultQuery("limit", "20")
	pageStr := c.DefaultQuery("page", "1")

	limit, err := strconv.Atoi(limitStr)
	if err != nil || limit <= 0 {
		limit = 20
	}

	page, err := strconv.Atoi(pageStr)
	if err != nil || page <= 0 {
		page = 1
	}

	offset := (page - 1) * limit

	notifs, total, err := h.notifService.GetNotifications(c.Request.Context(), userID.(uuid.UUID), limit, offset)
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to retrieve notifications",
		})
		return
	}

	totalPages := (int(total) + limit - 1) / limit

	c.JSON(http.StatusOK, dto.PaginatedResponse{
		Success:    true,
		Message:    "Notifications retrieved successfully",
		Data:       notifs,
		Page:       page,
		Limit:      limit,
		Total:      total,
		TotalPages: totalPages,
	})
}

// MarkAsRead godoc
// @Summary Mark notification as read
// @Description Mark a single notification as read by ID
// @Tags Notification
// @Accept json
// @Produce json
// @Param id path string true "Notification ID"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Notification marked as read"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/notification/read/{id} [patch]
func (h *NotificationHandler) MarkAsRead(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	notifIDStr := c.Param("id")
	notifID, err := uuid.Parse(notifIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: "Invalid notification ID format",
		})
		return
	}

	err = h.notifService.MarkAsRead(c.Request.Context(), userID.(uuid.UUID), notifID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to mark notification as read",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Notification marked as read",
	})
}

// MarkAllAsRead godoc
// @Summary Mark all notifications as read
// @Description Mark all unread notifications of user as read
// @Tags Notification
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "All notifications marked as read"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/notification/read-all [post]
func (h *NotificationHandler) MarkAllAsRead(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	err := h.notifService.MarkAllAsRead(c.Request.Context(), userID.(uuid.UUID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to mark all notifications as read",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "All notifications marked as read",
	})
}

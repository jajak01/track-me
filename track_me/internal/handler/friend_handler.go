package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/service"
	"github.com/desen/track-me/pkg/validator"
)

type FriendHandler struct {
	friendService service.FriendService
}

func NewFriendHandler(friendService service.FriendService) *FriendHandler {
	return &FriendHandler{friendService: friendService}
}

// SendFriendRequest godoc
// @Summary Send friend request
// @Description Initiate a friendship request to another user by email
// @Tags Friend
// @Accept json
// @Produce json
// @Param request body dto.SendFriendRequest true "Friend request payload"
// @Security BearerAuth
// @Success 201 {object} dto.APIResponse{data=dto.FriendRequestResponse} "Request sent"
// @Failure 400 {object} dto.APIResponse "Bad request (self friending or duplicate)"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/friend/request [post]
func (h *FriendHandler) SendFriendRequest(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.SendFriendRequest
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

	res, err := h.friendService.SendRequest(c.Request.Context(), userID.(uuid.UUID), req)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	c.JSON(http.StatusCreated, dto.APIResponse{
		Success: true,
		Message: "Friend request sent successfully",
		Data:    res,
	})
}

// RespondFriendRequest godoc
// @Summary Respond to friend request
// @Description Accept or reject a pending friend request
// @Tags Friend
// @Accept json
// @Produce json
// @Param request body dto.RespondFriendRequest true "Response payload"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Request responded successfully"
// @Failure 400 {object} dto.APIResponse "Validation or logical error"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/friend/respond [post]
func (h *FriendHandler) RespondFriendRequest(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.RespondFriendRequest
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

	err := h.friendService.RespondRequest(c.Request.Context(), userID.(uuid.UUID), req)
	if err != nil {
		c.JSON(http.StatusBadRequest, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	msg := "Friend request rejected"
	if req.Accept {
		msg = "Friend request accepted"
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: msg,
	})
}

// GetPendingRequests godoc
// @Summary Get pending requests
// @Description List all incoming pending friend requests for the user
// @Tags Friend
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=[]dto.FriendRequestResponse} "Success"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/friend/requests [get]
func (h *FriendHandler) GetPendingRequests(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	reqs, err := h.friendService.GetPendingRequests(c.Request.Context(), userID.(uuid.UUID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to get pending requests",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Pending requests retrieved successfully",
		Data:    reqs,
	})
}

// GetFriends godoc
// @Summary Get friends list
// @Description Retrieve a list of all friends of the user
// @Tags Friend
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=[]dto.UserResponse} "Success"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/friend/list [get]
func (h *FriendHandler) GetFriends(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	friends, err := h.friendService.GetFriends(c.Request.Context(), userID.(uuid.UUID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to retrieve friends",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Friends list retrieved successfully",
		Data:    friends,
	})
}

// RemoveFriend godoc
// @Summary Remove a friend
// @Description Terminate friendship with a user by ID
// @Tags Friend
// @Accept json
// @Produce json
// @Param id path string true "Friend User ID"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Friend removed successfully"
// @Failure 400 {object} dto.APIResponse "Bad request"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Router /api/v1/friend/remove/{id} [delete]
func (h *FriendHandler) RemoveFriend(c *gin.Context) {
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

	err = h.friendService.RemoveFriend(c.Request.Context(), userID.(uuid.UUID), friendID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to remove friend",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Friend removed successfully",
	})
}

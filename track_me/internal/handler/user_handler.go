package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/service"
	"github.com/desen/track-me/pkg/validator"
)

type UserHandler struct {
	userService service.UserService
}

func NewUserHandler(userService service.UserService) *UserHandler {
	return &UserHandler{userService: userService}
}

// GetProfile godoc
// @Summary Get user profile
// @Description Retrieve profile information of the authenticated user
// @Tags User
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=dto.UserResponse} "Success"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Failure 404 {object} dto.APIResponse "User not found"
// @Router /api/v1/user/profile [get]
func (h *UserHandler) GetProfile(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	profile, err := h.userService.GetProfile(c.Request.Context(), userID.(uuid.UUID))
	if err != nil {
		c.JSON(http.StatusNotFound, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Profile retrieved successfully",
		Data:    profile,
	})
}

// UpdateProfile godoc
// @Summary Update user profile
// @Description Update fields of authenticated user's profile dynamically
// @Tags User
// @Accept json
// @Produce json
// @Param request body dto.UpdateProfileRequest true "Profile fields to update"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse{data=dto.UserResponse} "Success"
// @Failure 400 {object} dto.APIResponse "Validation error"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Failure 404 {object} dto.APIResponse "User not found"
// @Router /api/v1/user/profile [patch]
func (h *UserHandler) UpdateProfile(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.UpdateProfileRequest
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

	profile, err := h.userService.UpdateProfile(c.Request.Context(), userID.(uuid.UUID), req)
	if err != nil {
		c.JSON(http.StatusNotFound, dto.APIResponse{
			Success: false,
			Message: err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Profile updated successfully",
		Data:    profile,
	})
}

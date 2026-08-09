package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/service"
	"github.com/desen/track-me/pkg/validator"
)

type AuthHandler struct {
	authService service.AuthService
}

func NewAuthHandler(authService service.AuthService) *AuthHandler {
	return &AuthHandler{authService: authService}
}

// Register godoc
// @Summary Register a new user
// @Description Create a new account with email, password, and display name
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body dto.RegisterRequest true "Register Request Details"
// @Success 201 {object} dto.APIResponse{data=dto.UserResponse} "Successfully registered"
// @Failure 400 {object} dto.APIResponse "Validation or duplicate email error"
// @Failure 500 {object} dto.APIResponse "Server error"
// @Router /api/v1/auth/register [post]
func (h *AuthHandler) Register(c *gin.Context) {
	var req dto.RegisterRequest
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

	userResp, err := h.authService.Register(c.Request.Context(), req, c.ClientIP(), c.Request.UserAgent())
	if err != nil {
		if err == service.ErrEmailExists {
			c.JSON(http.StatusBadRequest, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to register user",
		})
		return
	}

	c.JSON(http.StatusCreated, dto.APIResponse{
		Success: true,
		Message: "User registered successfully",
		Data:    userResp,
	})
}

// Login godoc
// @Summary Login user
// @Description Authenticate user and return token pair
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body dto.LoginRequest true "Login Credentials"
// @Success 200 {object} dto.APIResponse{data=dto.LoginResponse} "Successfully logged in"
// @Failure 400 {object} dto.APIResponse "Validation error"
// @Failure 401 {object} dto.APIResponse "Invalid credentials"
// @Failure 500 {object} dto.APIResponse "Server error"
// @Router /api/v1/auth/login [post]
func (h *AuthHandler) Login(c *gin.Context) {
	var req dto.LoginRequest
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

	loginResp, err := h.authService.Login(c.Request.Context(), req, c.ClientIP(), c.Request.UserAgent())
	if err != nil {
		if err == service.ErrInvalidPassword {
			c.JSON(http.StatusUnauthorized, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to log in",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Logged in successfully",
		Data:    loginResp,
	})
}

// RefreshToken godoc
// @Summary Refresh token pair
// @Description Get a new access and refresh token pair using a valid refresh token
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body dto.RefreshRequest true "Refresh Token Details"
// @Success 200 {object} dto.APIResponse{data=dto.LoginResponse} "Token refreshed"
// @Failure 400 {object} dto.APIResponse "Validation error"
// @Failure 401 {object} dto.APIResponse "Invalid or expired session"
// @Failure 500 {object} dto.APIResponse "Server error"
// @Router /api/v1/auth/refresh [post]
func (h *AuthHandler) RefreshToken(c *gin.Context) {
	var req dto.RefreshRequest
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

	refreshResp, err := h.authService.RefreshToken(c.Request.Context(), req, c.ClientIP(), c.Request.UserAgent())
	if err != nil {
		if err == service.ErrInvalidSession || err == service.ErrUserNotFound {
			c.JSON(http.StatusUnauthorized, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to refresh token",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Token refreshed successfully",
		Data:    refreshResp,
	})
}

// Logout godoc
// @Summary Logout user
// @Description Invalidate current user session and refresh token
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body dto.RefreshRequest true "Refresh Token to invalidate"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Successfully logged out"
// @Failure 400 {object} dto.APIResponse "Validation error"
// @Router /api/v1/auth/logout [post]
func (h *AuthHandler) Logout(c *gin.Context) {
	var req dto.RefreshRequest
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

	_ = h.authService.Logout(c.Request.Context(), req.RefreshToken, c.ClientIP(), c.Request.UserAgent())

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Logged out successfully",
	})
}

// ChangePassword godoc
// @Summary Change user password
// @Description Update password for the authenticated user and invalidate all other sessions
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body dto.ChangePasswordRequest true "Change Password Details"
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Password updated successfully"
// @Failure 400 {object} dto.APIResponse "Validation or mismatch error"
// @Failure 401 {object} dto.APIResponse "Unauthorized or incorrect old password"
// @Router /api/v1/auth/change-password [post]
func (h *AuthHandler) ChangePassword(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	var req dto.ChangePasswordRequest
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

	err := h.authService.ChangePassword(c.Request.Context(), userID.(uuid.UUID), req, c.ClientIP(), c.Request.UserAgent())
	if err != nil {
		if err == service.ErrOldPasswordWrong {
			c.JSON(http.StatusUnauthorized, dto.APIResponse{
				Success: false,
				Message: err.Error(),
			})
			return
		}
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to change password",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Password updated successfully",
	})
}

// DeleteAccount godoc
// @Summary Delete account
// @Description Permanently delete the authenticated user account and all session data
// @Tags Auth
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} dto.APIResponse "Account deleted successfully"
// @Failure 401 {object} dto.APIResponse "Unauthorized"
// @Failure 500 {object} dto.APIResponse "Server error"
// @Router /api/v1/auth/delete [delete]
func (h *AuthHandler) DeleteAccount(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, dto.APIResponse{
			Success: false,
			Message: "Unauthorized",
		})
		return
	}

	err := h.authService.DeleteAccount(c.Request.Context(), userID.(uuid.UUID), c.ClientIP(), c.Request.UserAgent())
	if err != nil {
		c.JSON(http.StatusInternalServerError, dto.APIResponse{
			Success: false,
			Message: "Failed to delete account",
		})
		return
	}

	c.JSON(http.StatusOK, dto.APIResponse{
		Success: true,
		Message: "Account deleted successfully",
	})
}

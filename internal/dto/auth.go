package dto

type RegisterRequest struct {
	Email       string `json:"email" validate:"required,email"`
	Password    string `json:"password" validate:"required,min=8,max=72"`
	DisplayName string `json:"display_name" validate:"required,min=2,max=100"`
}

type LoginRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required"`
}

type RefreshRequest struct {
	RefreshToken string `json:"refresh_token" validate:"required"`
}

type ChangePasswordRequest struct {
	OldPassword string `json:"old_password" validate:"required"`
	NewPassword string `json:"new_password" validate:"required,min=8,max=72"`
}

type LoginResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	ExpiresIn    int64  `json:"expires_in"`
	User         UserResponse `json:"user"`
}

type UserResponse struct {
	ID            string `json:"id"`
	Email         string `json:"email"`
	DisplayName   string `json:"display_name"`
	Avatar        string `json:"avatar"`
	Phone         string `json:"phone"`
	StatusMessage string `json:"status_message"`
	CreatedAt     string `json:"created_at"`
	UpdatedAt     string `json:"updated_at"`
}

type UpdateProfileRequest struct {
	DisplayName   *string `json:"display_name" validate:"omitempty,min=2,max=100"`
	Avatar        *string `json:"avatar" validate:"omitempty,max=500"`
	Phone         *string `json:"phone" validate:"omitempty,max=20"`
	StatusMessage *string `json:"status_message" validate:"omitempty,max=255"`
}

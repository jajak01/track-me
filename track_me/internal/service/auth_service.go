package service

import (
	"context"
	"errors"
	"time"

	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/models"
	"github.com/desen/track-me/internal/repository"
	"github.com/desen/track-me/pkg/jwt"
)

var (
	ErrEmailExists      = errors.New("email already registered")
	ErrInvalidPassword  = errors.New("invalid email or password")
	ErrUserNotFound     = errors.New("user not found")
	ErrInvalidSession   = errors.New("invalid or expired session")
	ErrOldPasswordWrong = errors.New("incorrect old password")
)

type AuthService interface {
	Register(ctx context.Context, req dto.RegisterRequest, ipAddress, userAgent string) (*dto.UserResponse, error)
	Login(ctx context.Context, req dto.LoginRequest, ipAddress, userAgent string) (*dto.LoginResponse, error)
	RefreshToken(ctx context.Context, req dto.RefreshRequest, ipAddress, userAgent string) (*dto.LoginResponse, error)
	Logout(ctx context.Context, token string, ipAddress, userAgent string) error
	ChangePassword(ctx context.Context, userID uuid.UUID, req dto.ChangePasswordRequest, ipAddress, userAgent string) error
	DeleteAccount(ctx context.Context, userID uuid.UUID, ipAddress, userAgent string) error
}

type authService struct {
	userRepo       repository.UserRepository
	refreshRepo    repository.RefreshTokenRepository
	sessionRepo    repository.SessionRepository
	auditRepo      repository.AuditLogRepository
	jwtService     *jwt.Service
}

func NewAuthService(
	userRepo repository.UserRepository,
	refreshRepo repository.RefreshTokenRepository,
	sessionRepo repository.SessionRepository,
	auditRepo repository.AuditLogRepository,
	jwtService *jwt.Service,
) AuthService {
	return &authService{
		userRepo:    userRepo,
		refreshRepo: refreshRepo,
		sessionRepo: sessionRepo,
		auditRepo:   auditRepo,
		jwtService:  jwtService,
	}
}

func (s *authService) Register(ctx context.Context, req dto.RegisterRequest, ipAddress, userAgent string) (*dto.UserResponse, error) {
	exists, err := s.userRepo.ExistsByEmail(ctx, req.Email)
	if err != nil {
		return nil, err
	}
	if exists {
		return nil, ErrEmailExists
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}

	user := &models.User{
		Email:        req.Email,
		PasswordHash: string(hashedPassword),
		DisplayName:  req.DisplayName,
	}

	if err := s.userRepo.Create(ctx, user); err != nil {
		return nil, err
	}

	// Create audit log
	_ = s.auditRepo.Create(ctx, &models.AuditLog{
		UserID:    user.ID,
		Action:    models.AuditRegister,
		IPAddress: ipAddress,
		UserAgent: userAgent,
		Details:   "User registered successfully",
	})

	return toUserResponse(user), nil
}

func (s *authService) Login(ctx context.Context, req dto.LoginRequest, ipAddress, userAgent string) (*dto.LoginResponse, error) {
	user, err := s.userRepo.FindByEmail(ctx, req.Email)
	if err != nil {
		return nil, ErrInvalidPassword
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		return nil, ErrInvalidPassword
	}

	tokenPair, err := s.jwtService.GenerateTokenPair(user.ID.String(), user.Email)
	if err != nil {
		return nil, err
	}

	expiresAt := time.Now().Add(s.jwtService.GetRefreshExpiry())

	rt := &models.RefreshToken{
		UserID:    user.ID,
		Token:     tokenPair.RefreshToken,
		ExpiresAt: expiresAt,
	}
	if err := s.refreshRepo.Create(ctx, rt); err != nil {
		return nil, err
	}

	// Create Session
	session := &models.Session{
		UserID:       user.ID,
		RefreshToken: tokenPair.RefreshToken,
		IPAddress:    ipAddress,
		UserAgent:    userAgent,
		ExpiresAt:    expiresAt,
	}
	if err := s.sessionRepo.Create(ctx, session); err != nil {
		return nil, err
	}

	// Create Audit Log
	_ = s.auditRepo.Create(ctx, &models.AuditLog{
		UserID:    user.ID,
		Action:    models.AuditLogin,
		IPAddress: ipAddress,
		UserAgent: userAgent,
		Details:   "User logged in successfully",
	})

	return &dto.LoginResponse{
		AccessToken:  tokenPair.AccessToken,
		RefreshToken: tokenPair.RefreshToken,
		ExpiresIn:    tokenPair.ExpiresIn,
		User:         *toUserResponse(user),
	}, nil
}

func (s *authService) RefreshToken(ctx context.Context, req dto.RefreshRequest, ipAddress, userAgent string) (*dto.LoginResponse, error) {
	claims, err := s.jwtService.ValidateRefreshToken(req.RefreshToken)
	if err != nil {
		return nil, ErrInvalidSession
	}

	// Check if token exists in repo
	rt, err := s.refreshRepo.FindByToken(ctx, req.RefreshToken)
	if err != nil || rt == nil {
		return nil, ErrInvalidSession
	}

	userUUID, err := uuid.Parse(claims.UserID)
	if err != nil {
		return nil, ErrInvalidSession
	}

	user, err := s.userRepo.FindByID(ctx, userUUID)
	if err != nil {
		return nil, ErrUserNotFound
	}

	// Generate new token pair
	tokenPair, err := s.jwtService.GenerateTokenPair(user.ID.String(), user.Email)
	if err != nil {
		return nil, err
	}

	// Delete old token and session
	_ = s.refreshRepo.DeleteByToken(ctx, req.RefreshToken)
	_ = s.sessionRepo.DeleteByToken(ctx, req.RefreshToken)

	// Save new Refresh Token
	expiresAt := time.Now().Add(s.jwtService.GetRefreshExpiry())

	newRt := &models.RefreshToken{
		UserID:    user.ID,
		Token:     tokenPair.RefreshToken,
		ExpiresAt: expiresAt,
	}
	if err := s.refreshRepo.Create(ctx, newRt); err != nil {
		return nil, err
	}

	// Create new session
	session := &models.Session{
		UserID:       user.ID,
		RefreshToken: tokenPair.RefreshToken,
		IPAddress:    ipAddress,
		UserAgent:    userAgent,
		ExpiresAt:    expiresAt,
	}
	if err := s.sessionRepo.Create(ctx, session); err != nil {
		return nil, err
	}

	// Audit Log
	_ = s.auditRepo.Create(ctx, &models.AuditLog{
		UserID:    user.ID,
		Action:    models.AuditRefreshToken,
		IPAddress: ipAddress,
		UserAgent: userAgent,
		Details:   "Token refreshed successfully",
	})

	return &dto.LoginResponse{
		AccessToken:  tokenPair.AccessToken,
		RefreshToken: tokenPair.RefreshToken,
		ExpiresIn:    tokenPair.ExpiresIn,
		User:         *toUserResponse(user),
	}, nil
}

func (s *authService) Logout(ctx context.Context, token string, ipAddress, userAgent string) error {
	claims, err := s.jwtService.ValidateRefreshToken(token)
	var userID uuid.UUID
	if err == nil {
		userID, _ = uuid.Parse(claims.UserID)
	}

	_ = s.refreshRepo.DeleteByToken(ctx, token)
	_ = s.sessionRepo.DeleteByToken(ctx, token)

	if userID != uuid.Nil {
		_ = s.auditRepo.Create(ctx, &models.AuditLog{
			UserID:    userID,
			Action:    models.AuditLogout,
			IPAddress: ipAddress,
			UserAgent: userAgent,
			Details:   "User logged out successfully",
		})
	}

	return nil
}

func (s *authService) ChangePassword(ctx context.Context, userID uuid.UUID, req dto.ChangePasswordRequest, ipAddress, userAgent string) error {
	user, err := s.userRepo.FindByID(ctx, userID)
	if err != nil {
		return ErrUserNotFound
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.OldPassword)); err != nil {
		return ErrOldPasswordWrong
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.NewPassword), bcrypt.DefaultCost)
	if err != nil {
		return err
	}

	user.PasswordHash = string(hashedPassword)
	if err := s.userRepo.Update(ctx, user); err != nil {
		return err
	}

	// Invalidate all tokens/sessions of this user for security
	_ = s.refreshRepo.DeleteByUserID(ctx, userID)
	_ = s.sessionRepo.DeleteByUserID(ctx, userID)

	_ = s.auditRepo.Create(ctx, &models.AuditLog{
		UserID:    userID,
		Action:    models.AuditChangePassword,
		IPAddress: ipAddress,
		UserAgent: userAgent,
		Details:   "Password changed successfully",
	})

	return nil
}

func (s *authService) DeleteAccount(ctx context.Context, userID uuid.UUID, ipAddress, userAgent string) error {
	user, err := s.userRepo.FindByID(ctx, userID)
	if err != nil {
		return ErrUserNotFound
	}

	// Perform user soft-delete
	if err := s.userRepo.Delete(ctx, userID); err != nil {
		return err
	}

	// Delete all session & tokens
	_ = s.refreshRepo.DeleteByUserID(ctx, userID)
	_ = s.sessionRepo.DeleteByUserID(ctx, userID)

	_ = s.auditRepo.Create(ctx, &models.AuditLog{
		UserID:    userID,
		Action:    models.AuditDeleteAccount,
		IPAddress: ipAddress,
		UserAgent: userAgent,
		Details:   "Account deleted successfully",
	})

	return nil
}

func toUserResponse(u *models.User) *dto.UserResponse {
	return &dto.UserResponse{
		ID:            u.ID.String(),
		Email:         u.Email,
		DisplayName:   u.DisplayName,
		Avatar:         u.Avatar,
		Phone:         u.Phone,
		StatusMessage: u.StatusMessage,
		CreatedAt:     u.CreatedAt.Format(time.RFC3339),
		UpdatedAt:     u.UpdatedAt.Format(time.RFC3339),
	}
}

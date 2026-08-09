package service

import (
	"context"

	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/repository"
)

type UserService interface {
	GetProfile(ctx context.Context, userID uuid.UUID) (*dto.UserResponse, error)
	UpdateProfile(ctx context.Context, userID uuid.UUID, req dto.UpdateProfileRequest) (*dto.UserResponse, error)
}

type userService struct {
	userRepo repository.UserRepository
}

func NewUserService(userRepo repository.UserRepository) UserService {
	return &userService{userRepo: userRepo}
}

func (s *userService) GetProfile(ctx context.Context, userID uuid.UUID) (*dto.UserResponse, error) {
	user, err := s.userRepo.FindByID(ctx, userID)
	if err != nil {
		return nil, ErrUserNotFound
	}
	return toUserResponse(user), nil
}

func (s *userService) UpdateProfile(ctx context.Context, userID uuid.UUID, req dto.UpdateProfileRequest) (*dto.UserResponse, error) {
	user, err := s.userRepo.FindByID(ctx, userID)
	if err != nil {
		return nil, ErrUserNotFound
	}

	if req.DisplayName != nil {
		user.DisplayName = *req.DisplayName
	}
	if req.Avatar != nil {
		user.Avatar = *req.Avatar
	}
	if req.Phone != nil {
		user.Phone = *req.Phone
	}
	if req.StatusMessage != nil {
		user.StatusMessage = *req.StatusMessage
	}

	if err := s.userRepo.Update(ctx, user); err != nil {
		return nil, err
	}

	return toUserResponse(user), nil
}

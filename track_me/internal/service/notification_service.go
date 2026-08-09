package service

import (
	"context"
	"time"

	"github.com/google/uuid"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/models"
	"github.com/desen/track-me/internal/repository"
)

type NotificationService interface {
	GetNotifications(ctx context.Context, userID uuid.UUID, limit, offset int) ([]dto.NotificationResponse, int64, error)
	MarkAsRead(ctx context.Context, userID, notifID uuid.UUID) error
	MarkAllAsRead(ctx context.Context, userID uuid.UUID) error
}

type notificationService struct {
	notifRepo repository.NotificationRepository
}

func NewNotificationService(notifRepo repository.NotificationRepository) NotificationService {
	return &notificationService{notifRepo: notifRepo}
}

func (s *notificationService) GetNotifications(ctx context.Context, userID uuid.UUID, limit, offset int) ([]dto.NotificationResponse, int64, error) {
	notifs, total, err := s.notifRepo.FindByUserID(ctx, userID, limit, offset)
	if err != nil {
		return nil, 0, err
	}

	responses := make([]dto.NotificationResponse, 0, len(notifs))
	for _, n := range notifs {
		responses = append(responses, *toNotificationResponse(&n))
	}

	return responses, total, nil
}

func (s *notificationService) MarkAsRead(ctx context.Context, userID, notifID uuid.UUID) error {
	return s.notifRepo.MarkAsRead(ctx, userID, notifID)
}

func (s *notificationService) MarkAllAsRead(ctx context.Context, userID uuid.UUID) error {
	return s.notifRepo.MarkAllAsRead(ctx, userID)
}

func toNotificationResponse(n *models.Notification) *dto.NotificationResponse {
	return &dto.NotificationResponse{
		ID:        n.ID.String(),
		UserID:    n.UserID.String(),
		Type:      string(n.Type),
		Title:     n.Title,
		Body:      n.Body,
		Data:      n.Data,
		Read:      n.Read,
		CreatedAt: n.CreatedAt.Format(time.RFC3339),
	}
}

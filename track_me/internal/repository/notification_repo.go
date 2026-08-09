package repository

import (
	"context"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/models"
)

type NotificationRepository interface {
	Create(ctx context.Context, notif *models.Notification) error
	FindByUserID(ctx context.Context, userID uuid.UUID, limit, offset int) ([]models.Notification, int64, error)
	MarkAsRead(ctx context.Context, userID uuid.UUID, notifID uuid.UUID) error
	MarkAllAsRead(ctx context.Context, userID uuid.UUID) error
}

type notificationRepository struct {
	db *gorm.DB
}

func NewNotificationRepository(db *gorm.DB) NotificationRepository {
	return &notificationRepository{db: db}
}

func (r *notificationRepository) Create(ctx context.Context, notif *models.Notification) error {
	return r.db.WithContext(ctx).Create(notif).Error
}

func (r *notificationRepository) FindByUserID(ctx context.Context, userID uuid.UUID, limit, offset int) ([]models.Notification, int64, error) {
	var notifs []models.Notification
	var total int64

	db := r.db.WithContext(ctx).Model(&models.Notification{}).Where("user_id = ?", userID)
	if err := db.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	err := db.Order("created_at desc").Limit(limit).Offset(offset).Find(&notifs).Error
	return notifs, total, err
}

func (r *notificationRepository) MarkAsRead(ctx context.Context, userID uuid.UUID, notifID uuid.UUID) error {
	return r.db.WithContext(ctx).Model(&models.Notification{}).
		Where("id = ? AND user_id = ?", notifID, userID).
		Update("read", true).Error
}

func (r *notificationRepository) MarkAllAsRead(ctx context.Context, userID uuid.UUID) error {
	return r.db.WithContext(ctx).Model(&models.Notification{}).
		Where("user_id = ? AND read = ?", userID, false).
		Update("read", true).Error
}

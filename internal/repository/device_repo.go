package repository

import (
	"context"
	"errors"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/models"
)

type DeviceRepository interface {
	Save(ctx context.Context, device *models.Device) error
	FindByUserID(ctx context.Context, userID uuid.UUID) ([]models.Device, error)
	FindByFCMToken(ctx context.Context, token string) (*models.Device, error)
	Delete(ctx context.Context, id uuid.UUID) error
}

type deviceRepository struct {
	db *gorm.DB
}

func NewDeviceRepository(db *gorm.DB) DeviceRepository {
	return &deviceRepository{db: db}
}

func (r *deviceRepository) Save(ctx context.Context, device *models.Device) error {
	return r.db.WithContext(ctx).Save(device).Error
}

func (r *deviceRepository) FindByUserID(ctx context.Context, userID uuid.UUID) ([]models.Device, error) {
	var devices []models.Device
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).Find(&devices).Error
	return devices, err
}

func (r *deviceRepository) FindByFCMToken(ctx context.Context, token string) (*models.Device, error) {
	var dev models.Device
	err := r.db.WithContext(ctx).Where("fcm_token = ?", token).First(&dev).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}
	return &dev, nil
}

func (r *deviceRepository) Delete(ctx context.Context, id uuid.UUID) error {
	return r.db.WithContext(ctx).Delete(&models.Device{}, "id = ?", id).Error
}

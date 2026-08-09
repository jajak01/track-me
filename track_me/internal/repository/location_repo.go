package repository

import (
	"context"
	"errors"
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/models"
)

type LocationRepository interface {
	// Location Permissions
	CreatePermission(ctx context.Context, perm *models.LocationPermission) error
	FindPermission(ctx context.Context, ownerID, viewerID uuid.UUID) (*models.LocationPermission, error)
	UpdatePermission(ctx context.Context, perm *models.LocationPermission) error
	FindGrantedPermissions(ctx context.Context, ownerID uuid.UUID) ([]models.LocationPermission, error)
	FindReceivedPermissions(ctx context.Context, viewerID uuid.UUID) ([]models.LocationPermission, error)

	// Locations
	CreateLocation(ctx context.Context, loc *models.Location) error
	GetLatestLocation(ctx context.Context, userID uuid.UUID) (*models.Location, error)
	GetLatestLocations(ctx context.Context, userIDs []uuid.UUID) ([]models.Location, error)
	GetLocationHistory(ctx context.Context, userID uuid.UUID, start, end time.Time) ([]models.Location, error)
	DeleteLocationHistory(ctx context.Context, userID uuid.UUID) error
}

type locationRepository struct {
	db *gorm.DB
}

func NewLocationRepository(db *gorm.DB) LocationRepository {
	return &locationRepository{db: db}
}

func (r *locationRepository) CreatePermission(ctx context.Context, perm *models.LocationPermission) error {
	return r.db.WithContext(ctx).Create(perm).Error
}

func (r *locationRepository) FindPermission(ctx context.Context, ownerID, viewerID uuid.UUID) (*models.LocationPermission, error) {
	var perm models.LocationPermission
	err := r.db.WithContext(ctx).Where("owner_id = ? AND viewer_id = ?", ownerID, viewerID).First(&perm).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}
	return &perm, nil
}

func (r *locationRepository) UpdatePermission(ctx context.Context, perm *models.LocationPermission) error {
	return r.db.WithContext(ctx).Save(perm).Error
}

func (r *locationRepository) FindGrantedPermissions(ctx context.Context, ownerID uuid.UUID) ([]models.LocationPermission, error) {
	var perms []models.LocationPermission
	err := r.db.WithContext(ctx).Where("owner_id = ? AND status = ?", ownerID, models.PermissionGranted).Find(&perms).Error
	return perms, err
}

func (r *locationRepository) FindReceivedPermissions(ctx context.Context, viewerID uuid.UUID) ([]models.LocationPermission, error) {
	var perms []models.LocationPermission
	err := r.db.WithContext(ctx).Where("viewer_id = ? AND status = ?", viewerID, models.PermissionGranted).Find(&perms).Error
	return perms, err
}

func (r *locationRepository) CreateLocation(ctx context.Context, loc *models.Location) error {
	return r.db.WithContext(ctx).Create(loc).Error
}

func (r *locationRepository) GetLatestLocation(ctx context.Context, userID uuid.UUID) (*models.Location, error) {
	var loc models.Location
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).Order("timestamp desc").First(&loc).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}
	return &loc, nil
}

func (r *locationRepository) GetLatestLocations(ctx context.Context, userIDs []uuid.UUID) ([]models.Location, error) {
	if len(userIDs) == 0 {
		return []models.Location{}, nil
	}
	var locs []models.Location
	// Use subquery to get latest location for each user in userIDs
	subQuery := r.db.Model(&models.Location{}).
		Select("user_id, MAX(timestamp) as max_ts").
		Where("user_id IN ?", userIDs).
		Group("user_id")

	err := r.db.WithContext(ctx).
		Joins("JOIN (?) as latest ON locations.user_id = latest.user_id AND locations.timestamp = latest.max_ts", subQuery).
		Find(&locs).Error

	return locs, err
}

func (r *locationRepository) GetLocationHistory(ctx context.Context, userID uuid.UUID, start, end time.Time) ([]models.Location, error) {
	var locs []models.Location
	err := r.db.WithContext(ctx).
		Where("user_id = ? AND timestamp >= ? AND timestamp <= ?", userID, start, end).
		Order("timestamp asc").
		Find(&locs).Error
	return locs, err
}

func (r *locationRepository) DeleteLocationHistory(ctx context.Context, userID uuid.UUID) error {
	return r.db.WithContext(ctx).Where("user_id = ?", userID).Delete(&models.Location{}).Error
}

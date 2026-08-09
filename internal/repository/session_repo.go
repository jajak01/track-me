package repository

import (
	"context"
	"errors"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/models"
)

type SessionRepository interface {
	Create(ctx context.Context, session *models.Session) error
	FindByToken(ctx context.Context, token string) (*models.Session, error)
	DeleteByUserID(ctx context.Context, userID uuid.UUID) error
	DeleteByToken(ctx context.Context, token string) error
}

type sessionRepository struct {
	db *gorm.DB
}

func NewSessionRepository(db *gorm.DB) SessionRepository {
	return &sessionRepository{db: db}
}

func (r *sessionRepository) Create(ctx context.Context, session *models.Session) error {
	return r.db.WithContext(ctx).Create(session).Error
}

func (r *sessionRepository) FindByToken(ctx context.Context, token string) (*models.Session, error) {
	var s models.Session
	err := r.db.WithContext(ctx).Where("refresh_token = ?", token).First(&s).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}
	return &s, nil
}

func (r *sessionRepository) DeleteByUserID(ctx context.Context, userID uuid.UUID) error {
	return r.db.WithContext(ctx).Where("user_id = ?", userID).Delete(&models.Session{}).Error
}

func (r *sessionRepository) DeleteByToken(ctx context.Context, token string) error {
	return r.db.WithContext(ctx).Where("refresh_token = ?", token).Delete(&models.Session{}).Error
}

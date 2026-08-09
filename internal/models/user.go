package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type User struct {
	ID           uuid.UUID      `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	Email        string         `gorm:"uniqueIndex;not null;size:255" json:"email"`
	PasswordHash string         `gorm:"not null" json:"-"`
	DisplayName  string         `gorm:"not null;size:100" json:"display_name"`
	Avatar       string         `gorm:"size:500" json:"avatar"`
	Phone        string         `gorm:"size:20" json:"phone"`
	StatusMessage string        `gorm:"size:255" json:"status_message"`
	CreatedAt    time.Time      `json:"created_at"`
	UpdatedAt    time.Time      `json:"updated_at"`
	DeletedAt    gorm.DeletedAt `gorm:"index" json:"-"`
}

func (User) TableName() string {
	return "users"
}

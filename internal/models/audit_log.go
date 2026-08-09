package models

import (
	"time"

	"github.com/google/uuid"
)

type AuditAction string

const (
	AuditRegister       AuditAction = "register"
	AuditLogin          AuditAction = "login"
	AuditLogout         AuditAction = "logout"
	AuditRefreshToken   AuditAction = "refresh_token"
	AuditChangePassword AuditAction = "change_password"
	AuditDeleteAccount  AuditAction = "delete_account"
)

type AuditLog struct {
	ID        uuid.UUID   `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	UserID    uuid.UUID   `gorm:"type:uuid;not null;index" json:"user_id"`
	Action    AuditAction `gorm:"not null;size:50" json:"action"`
	IPAddress string      `gorm:"size:45" json:"ip_address"`
	UserAgent string      `gorm:"size:500" json:"user_agent"`
	Details   string      `gorm:"type:text" json:"details"`
	CreatedAt time.Time   `json:"created_at"`
}

func (AuditLog) TableName() string {
	return "audit_logs"
}

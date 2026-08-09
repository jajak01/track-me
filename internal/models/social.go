package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type FriendRequestStatus string

const (
	FriendRequestPending  FriendRequestStatus = "pending"
	FriendRequestAccepted FriendRequestStatus = "accepted"
	FriendRequestRejected FriendRequestStatus = "rejected"
)

type FriendRequest struct {
	ID         uuid.UUID           `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	SenderID   uuid.UUID           `gorm:"type:uuid;not null;index" json:"sender_id"`
	ReceiverID uuid.UUID           `gorm:"type:uuid;not null;index" json:"receiver_id"`
	Status     FriendRequestStatus `gorm:"not null;size:20;default:pending" json:"status"`
	CreatedAt  time.Time           `json:"created_at"`
	UpdatedAt  time.Time           `json:"updated_at"`
}

func (FriendRequest) TableName() string {
	return "friend_requests"
}

type Friendship struct {
	ID        uuid.UUID `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	UserA     uuid.UUID `gorm:"type:uuid;not null;index" json:"user_a"`
	UserB     uuid.UUID `gorm:"type:uuid;not null;index" json:"user_b"`
	CreatedAt time.Time `json:"created_at"`
}

func (Friendship) TableName() string {
	return "friendships"
}

type BlockedUser struct {
	ID        uuid.UUID `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	BlockerID uuid.UUID `gorm:"type:uuid;not null;index" json:"blocker_id"`
	BlockedID uuid.UUID `gorm:"type:uuid;not null;index" json:"blocked_id"`
	CreatedAt time.Time `json:"created_at"`
}

func (BlockedUser) TableName() string {
	return "blocked_users"
}

type PermissionStatus string

const (
	PermissionGranted PermissionStatus = "granted"
	PermissionRevoked PermissionStatus = "revoked"
)

type LocationPermission struct {
	ID        uuid.UUID        `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	OwnerID   uuid.UUID        `gorm:"type:uuid;not null;index" json:"owner_id"`
	ViewerID  uuid.UUID        `gorm:"type:uuid;not null;index" json:"viewer_id"`
	Status    PermissionStatus `gorm:"not null;size:20;default:granted" json:"status"`
	GrantedAt time.Time        `json:"granted_at"`
	RevokedAt *time.Time       `json:"revoked_at"`
}

func (LocationPermission) TableName() string {
	return "location_permissions"
}

type ActivityType string

const (
	ActivityUnknown ActivityType = "unknown"
	ActivityStill   ActivityType = "still"
	ActivityWalking ActivityType = "walking"
	ActivityRunning ActivityType = "running"
	ActivityCycling ActivityType = "cycling"
	ActivityDriving ActivityType = "driving"
)

type Location struct {
	ID           uuid.UUID    `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	UserID       uuid.UUID    `gorm:"type:uuid;not null;index" json:"user_id"`
	Latitude     float64      `gorm:"not null" json:"latitude"`
	Longitude    float64      `gorm:"not null" json:"longitude"`
	Accuracy     float64      `json:"accuracy"`
	Altitude     float64      `json:"altitude"`
	Bearing      float64      `json:"bearing"`
	Speed        float64      `json:"speed"`
	Battery      int          `json:"battery"`
	Charging     bool         `json:"charging"`
	Activity     ActivityType `gorm:"size:20;default:unknown" json:"activity"`
	MockLocation bool         `gorm:"default:false" json:"mock_location"`
	GPSProvider  string       `gorm:"size:50" json:"gps_provider"`
	Timestamp    time.Time    `gorm:"not null;index" json:"timestamp"`
	CreatedAt    time.Time    `json:"created_at"`
}

func (Location) TableName() string {
	return "locations"
}

type Device struct {
	ID         uuid.UUID `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	UserID     uuid.UUID `gorm:"type:uuid;not null;index" json:"user_id"`
	DeviceName string    `gorm:"size:100" json:"device_name"`
	Platform   string    `gorm:"size:20" json:"platform"`
	FCMToken   string    `gorm:"size:500" json:"-"`
	LastSeenAt time.Time `json:"last_seen_at"`
	CreatedAt  time.Time `json:"created_at"`
	UpdatedAt  time.Time `json:"updated_at"`
}

func (Device) TableName() string {
	return "devices"
}

type NotificationType string

const (
	NotifFriendRequest   NotificationType = "friend_request"
	NotifFriendAccepted  NotificationType = "friend_accepted"
	NotifSharingRequest  NotificationType = "sharing_request"
	NotifSharingApproved NotificationType = "sharing_approved"
	NotifSharingRevoked  NotificationType = "sharing_revoked"
	NotifSOS             NotificationType = "sos"
	NotifArrival         NotificationType = "arrival"
)

type Notification struct {
	ID        uuid.UUID        `gorm:"type:uuid;primaryKey;default:gen_random_uuid()" json:"id"`
	UserID    uuid.UUID        `gorm:"type:uuid;not null;index" json:"user_id"`
	Type      NotificationType `gorm:"not null;size:30" json:"type"`
	Title     string           `gorm:"not null;size:255" json:"title"`
	Body      string           `gorm:"type:text" json:"body"`
	Data      string           `gorm:"type:jsonb" json:"data"`
	Read      bool             `gorm:"default:false" json:"read"`
	CreatedAt time.Time        `json:"created_at"`
}

func (n *Notification) BeforeCreate(tx *gorm.DB) error {
	if n.Data == "" {
		n.Data = "{}"
	}
	return nil
}

func (Notification) TableName() string {
	return "notifications"
}

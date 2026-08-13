package service

import (
	"context"
	"encoding/json"
	"errors"
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/models"
	"github.com/desen/track-me/internal/repository"
)

var (
	ErrNotFriends         = errors.New("you are not friends with this user")
	ErrSharingNotEnabled  = errors.New("mutual location sharing is not active with this user")
	ErrNotificationInvalid= errors.New("invalid or mismatching notification")
)

type LocationService interface {
	RequestSharing(ctx context.Context, requesterID, friendID uuid.UUID) error
	ApproveSharing(ctx context.Context, approverID, notificationID uuid.UUID, approve bool) error
	RevokeSharing(ctx context.Context, revokerID, friendID uuid.UUID) error
	UpdateLocation(ctx context.Context, userID uuid.UUID, req dto.LocationUpdateRequest) (*dto.LocationResponse, error)
	GetCurrentLocation(ctx context.Context, viewerID, ownerID uuid.UUID) (*dto.LocationResponse, error)
	GetLocationHistory(ctx context.Context, viewerID, ownerID uuid.UUID, start, end time.Time) ([]dto.LocationResponse, error)
	IsSharingActive(ctx context.Context, userA, userB uuid.UUID) (bool, error)
}

type locationService struct {
	db           *gorm.DB
	locationRepo repository.LocationRepository
	friendRepo   repository.FriendRepository
	userRepo     repository.UserRepository
	notifRepo    repository.NotificationRepository
}

func NewLocationService(
	db *gorm.DB,
	locationRepo repository.LocationRepository,
	friendRepo repository.FriendRepository,
	userRepo repository.UserRepository,
	notifRepo repository.NotificationRepository,
) LocationService {
	return &locationService{
		db:           db,
		locationRepo: locationRepo,
		friendRepo:   friendRepo,
		userRepo:     userRepo,
		notifRepo:    notifRepo,
	}
}

func (s *locationService) RequestSharing(ctx context.Context, requesterID, friendID uuid.UUID) error {
	// Verify they are friends
	areFriends, err := s.friendRepo.ExistsFriendship(ctx, requesterID, friendID)
	if err != nil {
		return err
	}
	if !areFriends {
		return ErrNotFriends
	}

	requester, err := s.userRepo.FindByID(ctx, requesterID)
	if err != nil {
		return ErrUserNotFound
	}

	// Create JSON data containing requester ID
	dataMap := map[string]string{
		"requester_id": requesterID.String(),
	}
	dataBytes, _ := json.Marshal(dataMap)

	// Create sharing_request notification for the friend
	notif := &models.Notification{
		UserID: friendID,
		Type:   models.NotifSharingRequest,
		Title:  "Location Sharing Request",
		Body:   requester.DisplayName + " requested to share locations with you.",
		Data:   string(dataBytes),
	}

	return s.notifRepo.Create(ctx, notif)
}

func (s *locationService) ApproveSharing(ctx context.Context, approverID, notificationID uuid.UUID, approve bool) error {
	// We run this in a transaction
	return s.db.Transaction(func(tx *gorm.DB) error {
		txNotifRepo := repository.NewNotificationRepository(tx)
		txLocationRepo := repository.NewLocationRepository(tx)
		txUserRepo := repository.NewUserRepository(tx)

		// Fetch and check notification
		// Note: since notifications are not queried via a rich FindByID repo method, we can query it directly or do it through custom GORM if needed.
		// Wait, our NotificationRepository interface has: Create, FindByUserID, MarkAsRead, MarkAllAsRead.
		// Let's query the notification from GORM database directly inside the transaction, or use tx.First
		var notif models.Notification
		if err := tx.First(&notif, "id = ?", notificationID).Error; err != nil {
			return ErrNotificationInvalid
		}

		if notif.UserID != approverID || notif.Type != models.NotifSharingRequest {
			return ErrNotificationInvalid
		}

		// Parse data
		var dataMap map[string]string
		if err := json.Unmarshal([]byte(notif.Data), &dataMap); err != nil {
			return ErrNotificationInvalid
		}

		requesterIDStr, ok := dataMap["requester_id"]
		if !ok {
			return ErrNotificationInvalid
		}

		requesterID, err := uuid.Parse(requesterIDStr)
		if err != nil {
			return ErrNotificationInvalid
		}

		// Mark request notification as read
		_ = txNotifRepo.MarkAsRead(ctx, approverID, notificationID)

		if approve {
			// Grant location permission: approverID (owner) shares location with requesterID (viewer)
			perm, err := txLocationRepo.FindPermission(ctx, approverID, requesterID)
			if err != nil {
				return err
			}

			if perm == nil {
				perm = &models.LocationPermission{
					OwnerID:   approverID,
					ViewerID:  requesterID,
					Status:    models.PermissionGranted,
					GrantedAt: time.Now(),
				}
				if err := txLocationRepo.CreatePermission(ctx, perm); err != nil {
					return err
				}
			} else if perm.Status != models.PermissionGranted {
				perm.Status = models.PermissionGranted
				perm.GrantedAt = time.Now()
				perm.RevokedAt = nil
				if err := txLocationRepo.UpdatePermission(ctx, perm); err != nil {
					return err
				}
			}

			// Also, automatically grant the opposite permission to make it fully mutual if we want,
			// or check if it exists. In the privacy-focused model, let's grant mutual sharing explicitly!
			// "Location sharing ONLY works if BOTH users agree."
			// When B approves, it grants permission B -> A. If A -> B is not yet granted, we also grant or check.
			// Let's grant A -> B as well since A initiated the mutual tracking request!
			// Yes! "User A requests location sharing -> User B approves -> Tracking enabled."
			// Since A requested it, A already consented to share location with B, and B's approval consents B to share with A.
			// So BOTH permissions (A -> B and B -> A) should be marked as granted!
			// This makes the flow extremely smooth and matches the plan description perfectly.
			oppositePerm, err := txLocationRepo.FindPermission(ctx, requesterID, approverID)
			if err != nil {
				return err
			}

			if oppositePerm == nil {
				oppositePerm = &models.LocationPermission{
					OwnerID:   requesterID,
					ViewerID:  approverID,
					Status:    models.PermissionGranted,
					GrantedAt: time.Now(),
				}
				if err := txLocationRepo.CreatePermission(ctx, oppositePerm); err != nil {
					return err
				}
			} else if oppositePerm.Status != models.PermissionGranted {
				oppositePerm.Status = models.PermissionGranted
				oppositePerm.GrantedAt = time.Now()
				oppositePerm.RevokedAt = nil
				if err := txLocationRepo.UpdatePermission(ctx, oppositePerm); err != nil {
					return err
				}
			}

			// Send sharing_approved notification to requester
			approver, err := txUserRepo.FindByID(ctx, approverID)
			if err == nil {
				_ = txNotifRepo.Create(ctx, &models.Notification{
					UserID: requesterID,
					Type:   models.NotifSharingApproved,
					Title:  "Location Sharing Enabled",
					Body:   approver.DisplayName + " approved your request. Mutual location tracking is now active.",
				})
			}
		}

		return nil
	})
}

func (s *locationService) RevokeSharing(ctx context.Context, revokerID, friendID uuid.UUID) error {
	// Revoke sharing in BOTH directions or only the revoking direction?
	// If one user revokes, the sharing is no longer mutual, so tracking stops.
	// To be safe and respect privacy, let's revoke both permissions to completely clear the state!
	return s.db.Transaction(func(tx *gorm.DB) error {
		txLocationRepo := repository.NewLocationRepository(tx)
		txNotifRepo := repository.NewNotificationRepository(tx)
		txUserRepo := repository.NewUserRepository(tx)

		now := time.Now()

		p1, err := txLocationRepo.FindPermission(ctx, revokerID, friendID)
		if err == nil && p1 != nil && p1.Status == models.PermissionGranted {
			p1.Status = models.PermissionRevoked
			p1.RevokedAt = &now
			_ = txLocationRepo.UpdatePermission(ctx, p1)
		}

		p2, err := txLocationRepo.FindPermission(ctx, friendID, revokerID)
		if err == nil && p2 != nil && p2.Status == models.PermissionGranted {
			p2.Status = models.PermissionRevoked
			p2.RevokedAt = &now
			_ = txLocationRepo.UpdatePermission(ctx, p2)
		}

		// Notify the other user
		revoker, err := txUserRepo.FindByID(ctx, revokerID)
		if err == nil {
			_ = txNotifRepo.Create(ctx, &models.Notification{
				UserID: friendID,
				Type:   models.NotifSharingRevoked,
				Title:  "Location Sharing Revoked",
				Body:   revoker.DisplayName + " has stopped location sharing.",
			})
		}

		return nil
	})
}

func (s *locationService) UpdateLocation(ctx context.Context, userID uuid.UUID, req dto.LocationUpdateRequest) (*dto.LocationResponse, error) {
	ts := time.Now().UTC()
	if req.Timestamp != "" {
		if parsed, err := time.Parse(time.RFC3339, req.Timestamp); err == nil {
			ts = parsed.UTC()
		}
	}

	loc := &models.Location{
		UserID:       userID,
		Latitude:     req.Latitude,
		Longitude:    req.Longitude,
		Accuracy:     req.Accuracy,
		Altitude:     req.Altitude,
		Bearing:      req.Bearing,
		Speed:        req.Speed,
		Battery:      req.BatteryPercentage,
		Charging:     req.IsCharging,
		Activity:     models.ActivityType(req.ActivityType),
		MockLocation: req.IsMock,
		Timestamp:    ts,
	}

	if err := s.locationRepo.CreateLocation(ctx, loc); err != nil {
		return nil, err
	}

	return toLocationResponse(loc), nil
}

func (s *locationService) IsSharingActive(ctx context.Context, userA, userB uuid.UUID) (bool, error) {
	// Sharing is active ONLY if both have granted permission to each other
	p1, err := s.locationRepo.FindPermission(ctx, userA, userB)
	if err != nil {
		return false, err
	}
	if p1 == nil || p1.Status != models.PermissionGranted {
		return false, nil
	}

	p2, err := s.locationRepo.FindPermission(ctx, userB, userA)
	if err != nil {
		return false, err
	}
	if p2 == nil || p2.Status != models.PermissionGranted {
		return false, nil
	}

	return true, nil
}

func (s *locationService) GetCurrentLocation(ctx context.Context, viewerID, ownerID uuid.UUID) (*dto.LocationResponse, error) {
	// Verify they are friends
	areFriends, err := s.friendRepo.ExistsFriendship(ctx, viewerID, ownerID)
	if err != nil {
		return nil, err
	}
	if !areFriends {
		return nil, ErrNotFriends
	}

	// Verify mutual sharing is active
	active, err := s.IsSharingActive(ctx, viewerID, ownerID)
	if err != nil {
		return nil, err
	}
	if !active {
		return nil, ErrSharingNotEnabled
	}

	loc, err := s.locationRepo.GetLatestLocation(ctx, ownerID)
	if err != nil {
		return nil, err
	}
	if loc == nil {
		return nil, errors.New("no location updates found for this user")
	}

	return toLocationResponse(loc), nil
}

func (s *locationService) GetLocationHistory(ctx context.Context, viewerID, ownerID uuid.UUID, start, end time.Time) ([]dto.LocationResponse, error) {
	// Verify they are friends
	areFriends, err := s.friendRepo.ExistsFriendship(ctx, viewerID, ownerID)
	if err != nil {
		return nil, err
	}
	if !areFriends {
		return nil, ErrNotFriends
	}

	// Verify mutual sharing is active
	active, err := s.IsSharingActive(ctx, viewerID, ownerID)
	if err != nil {
		return nil, err
	}
	if !active {
		return nil, ErrSharingNotEnabled
	}

	locs, err := s.locationRepo.GetLocationHistory(ctx, ownerID, start, end)
	if err != nil {
		return nil, err
	}

	responses := make([]dto.LocationResponse, 0, len(locs))
	for _, l := range locs {
		responses = append(responses, *toLocationResponse(&l))
	}

	return responses, nil
}

func toLocationResponse(l *models.Location) *dto.LocationResponse {
	return &dto.LocationResponse{
		UserID:            l.UserID.String(),
		Latitude:          l.Latitude,
		Longitude:         l.Longitude,
		Accuracy:          l.Accuracy,
		Altitude:          l.Altitude,
		Bearing:           l.Bearing,
		Speed:             l.Speed,
		BatteryPercentage: l.Battery,
		IsCharging:        l.Charging,
		ActivityType:      string(l.Activity),
		IsMock:            l.MockLocation,
		Timestamp:         l.Timestamp.Format(time.RFC3339),
	}
}

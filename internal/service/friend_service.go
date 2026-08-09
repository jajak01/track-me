package service

import (
	"context"
	"errors"
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/dto"
	"github.com/desen/track-me/internal/models"
	"github.com/desen/track-me/internal/repository"
)

var (
	ErrAlreadyFriends      = errors.New("you are already friends with this user")
	ErrFriendRequestExists = errors.New("a friend request already exists between you")
	ErrCannotFriendSelf    = errors.New("you cannot friend yourself")
	ErrRequestNotFound     = errors.New("friend request not found")
	ErrNotRequestReceiver  = errors.New("you are not the receiver of this friend request")
	ErrRequestNotPending   = errors.New("friend request is not pending")
)

type FriendService interface {
	SendRequest(ctx context.Context, senderID uuid.UUID, req dto.SendFriendRequest) (*dto.FriendRequestResponse, error)
	RespondRequest(ctx context.Context, receiverID uuid.UUID, req dto.RespondFriendRequest) error
	GetPendingRequests(ctx context.Context, userID uuid.UUID) ([]dto.FriendRequestResponse, error)
	GetFriends(ctx context.Context, userID uuid.UUID) ([]dto.UserResponse, error)
	RemoveFriend(ctx context.Context, userID, friendID uuid.UUID) error
	BlockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error
	UnblockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error
	GetBlockedUsers(ctx context.Context, userID uuid.UUID) ([]dto.UserResponse, error)
}

type friendService struct {
	db           *gorm.DB
	friendRepo   repository.FriendRepository
	userRepo     repository.UserRepository
	locationRepo repository.LocationRepository
	notifRepo    repository.NotificationRepository
}

func NewFriendService(
	db *gorm.DB,
	friendRepo repository.FriendRepository,
	userRepo repository.UserRepository,
	locationRepo repository.LocationRepository,
	notifRepo repository.NotificationRepository,
) FriendService {
	return &friendService{
		db:           db,
		friendRepo:   friendRepo,
		userRepo:     userRepo,
		locationRepo: locationRepo,
		notifRepo:    notifRepo,
	}
}

func (s *friendService) SendRequest(ctx context.Context, senderID uuid.UUID, req dto.SendFriendRequest) (*dto.FriendRequestResponse, error) {
	sender, err := s.userRepo.FindByID(ctx, senderID)
	if err != nil {
		return nil, ErrUserNotFound
	}

	receiver, err := s.userRepo.FindByEmail(ctx, req.ReceiverEmail)
	if err != nil {
		return nil, errors.New("receiver email not found")
	}

	if sender.ID == receiver.ID {
		return nil, ErrCannotFriendSelf
	}

	// Check if already friends
	areFriends, err := s.friendRepo.ExistsFriendship(ctx, sender.ID, receiver.ID)
	if err != nil {
		return nil, err
	}
	if areFriends {
		return nil, ErrAlreadyFriends
	}

	// Check if friend request already exists
	existingReq, err := s.friendRepo.FindRequestBetween(ctx, sender.ID, receiver.ID)
	if err != nil {
		return nil, err
	}
	if existingReq != nil {
		if existingReq.Status == models.FriendRequestPending {
			return nil, ErrFriendRequestExists
		}
		// If rejected, we can allow sending a new one, but let's delete or reuse the rejected one.
		// Let's reset the status to pending and save it.
		existingReq.Status = models.FriendRequestPending
		existingReq.SenderID = sender.ID
		existingReq.ReceiverID = receiver.ID
		if err := s.friendRepo.UpdateRequest(ctx, existingReq); err != nil {
			return nil, err
		}

		// Notify receiver
		_ = s.notifRepo.Create(ctx, &models.Notification{
			UserID: receiver.ID,
			Type:   models.NotifFriendRequest,
			Title:  "New Friend Request",
			Body:   sender.DisplayName + " sent you a friend request.",
		})

		return &dto.FriendRequestResponse{
			ID:        existingReq.ID.String(),
			Sender:    *toUserResponse(sender),
			Receiver:  *toUserResponse(receiver),
			Status:    string(existingReq.Status),
			CreatedAt: existingReq.CreatedAt.Format(time.RFC3339),
		}, nil
	}

	// Create new friend request
	friendReq := &models.FriendRequest{
		SenderID:   sender.ID,
		ReceiverID: receiver.ID,
		Status:     models.FriendRequestPending,
	}

	if err := s.friendRepo.CreateRequest(ctx, friendReq); err != nil {
		return nil, err
	}

	// Notify receiver
	_ = s.notifRepo.Create(ctx, &models.Notification{
		UserID: receiver.ID,
		Type:   models.NotifFriendRequest,
		Title:  "New Friend Request",
		Body:   sender.DisplayName + " sent you a friend request.",
	})

	return &dto.FriendRequestResponse{
		ID:        friendReq.ID.String(),
		Sender:    *toUserResponse(sender),
		Receiver:  *toUserResponse(receiver),
		Status:    string(friendReq.Status),
		CreatedAt: friendReq.CreatedAt.Format(time.RFC3339),
	}, nil
}

func (s *friendService) RespondRequest(ctx context.Context, receiverID uuid.UUID, req dto.RespondFriendRequest) error {
	reqUUID, err := uuid.Parse(req.RequestID)
	if err != nil {
		return errors.New("invalid request ID format")
	}

	// Transaction to respond and update status
	return s.db.Transaction(func(tx *gorm.DB) error {
		txFriendRepo := repository.NewFriendRepository(tx)
		txNotifRepo := repository.NewNotificationRepository(tx)

		friendReq, err := txFriendRepo.FindRequestByID(ctx, reqUUID)
		if err != nil {
			return ErrRequestNotFound
		}

		if friendReq.ReceiverID != receiverID {
			return ErrNotRequestReceiver
		}

		if friendReq.Status != models.FriendRequestPending {
			return ErrRequestNotPending
		}

		if req.Accept {
			friendReq.Status = models.FriendRequestAccepted
			if err := txFriendRepo.UpdateRequest(ctx, friendReq); err != nil {
				return err
			}

			// Create Friendship
			friendship := &models.Friendship{
				UserA: friendReq.SenderID,
				UserB: friendReq.ReceiverID,
			}
			if err := txFriendRepo.CreateFriendship(ctx, friendship); err != nil {
				return err
			}

			// Fetch sender details to send notification from receiver to sender
			receiver, err := s.userRepo.FindByID(ctx, receiverID)
			if err == nil {
				_ = txNotifRepo.Create(ctx, &models.Notification{
					UserID: friendReq.SenderID,
					Type:   models.NotifFriendAccepted,
					Title:  "Friend Request Accepted",
					Body:   receiver.DisplayName + " accepted your friend request.",
				})
			}
		} else {
			friendReq.Status = models.FriendRequestRejected
			if err := txFriendRepo.UpdateRequest(ctx, friendReq); err != nil {
				return err
			}
		}

		return nil
	})
}

func (s *friendService) GetPendingRequests(ctx context.Context, userID uuid.UUID) ([]dto.FriendRequestResponse, error) {
	reqs, err := s.friendRepo.FindPendingRequests(ctx, userID)
	if err != nil {
		return nil, err
	}

	responses := make([]dto.FriendRequestResponse, 0, len(reqs))
	for _, r := range reqs {
		sender, err := s.userRepo.FindByID(ctx, r.SenderID)
		if err != nil {
			continue
		}
		receiver, err := s.userRepo.FindByID(ctx, r.ReceiverID)
		if err != nil {
			continue
		}

		responses = append(responses, dto.FriendRequestResponse{
			ID:        r.ID.String(),
			Sender:    *toUserResponse(sender),
			Receiver:  *toUserResponse(receiver),
			Status:    string(r.Status),
			CreatedAt: r.CreatedAt.Format(time.RFC3339),
		})
	}

	return responses, nil
}

func (s *friendService) GetFriends(ctx context.Context, userID uuid.UUID) ([]dto.UserResponse, error) {
	friends, err := s.friendRepo.FindFriends(ctx, userID)
	if err != nil {
		return nil, err
	}

	responses := make([]dto.UserResponse, 0, len(friends))
	for _, f := range friends {
		responses = append(responses, *toUserResponse(&f))
	}

	return responses, nil
}

func (s *friendService) RemoveFriend(ctx context.Context, userID, friendID uuid.UUID) error {
	// Transaction to remove friendship and revoke mutual sharing permissions
	return s.db.Transaction(func(tx *gorm.DB) error {
		txFriendRepo := repository.NewFriendRepository(tx)
		txLocationRepo := repository.NewLocationRepository(tx)

		// Delete Friendship
		if err := txFriendRepo.DeleteFriendship(ctx, userID, friendID); err != nil {
			return err
		}

		// Revoke location permissions (both directions)
		p1, err := txLocationRepo.FindPermission(ctx, userID, friendID)
		if err == nil && p1 != nil {
			p1.Status = models.PermissionRevoked
			now := time.Now()
			p1.RevokedAt = &now
			_ = txLocationRepo.UpdatePermission(ctx, p1)
		}

		p2, err := txLocationRepo.FindPermission(ctx, friendID, userID)
		if err == nil && p2 != nil {
			p2.Status = models.PermissionRevoked
			now := time.Now()
			p2.RevokedAt = &now
			_ = txLocationRepo.UpdatePermission(ctx, p2)
		}

		return nil
	})
}

func (s *friendService) BlockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error {
	if blockerID == blockedID {
		return ErrCannotFriendSelf
	}

	// Remove friendship if exists
	_ = s.friendRepo.DeleteFriendship(ctx, blockerID, blockedID)

	// Revoke sharing permissions if any
	now := time.Now()
	p1, err := s.locationRepo.FindPermission(ctx, blockerID, blockedID)
	if err == nil && p1 != nil {
		p1.Status = models.PermissionRevoked
		p1.RevokedAt = &now
		_ = s.locationRepo.UpdatePermission(ctx, p1)
	}
	p2, err := s.locationRepo.FindPermission(ctx, blockedID, blockerID)
	if err == nil && p2 != nil {
		p2.Status = models.PermissionRevoked
		p2.RevokedAt = &now
		_ = s.locationRepo.UpdatePermission(ctx, p2)
	}

	return s.friendRepo.BlockUser(ctx, blockerID, blockedID)
}

func (s *friendService) UnblockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error {
	return s.friendRepo.UnblockUser(ctx, blockerID, blockedID)
}

func (s *friendService) GetBlockedUsers(ctx context.Context, userID uuid.UUID) ([]dto.UserResponse, error) {
	users, err := s.friendRepo.FindBlockedUsers(ctx, userID)
	if err != nil {
		return nil, err
	}

	responses := make([]dto.UserResponse, 0, len(users))
	for _, u := range users {
		responses = append(responses, *toUserResponse(&u))
	}
	return responses, nil
}

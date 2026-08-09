package repository

import (
	"context"
	"errors"

	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/desen/track-me/internal/models"
)

type FriendRepository interface {
	// Friend Requests
	CreateRequest(ctx context.Context, req *models.FriendRequest) error
	FindRequestByID(ctx context.Context, id uuid.UUID) (*models.FriendRequest, error)
	FindRequestBetween(ctx context.Context, senderID, receiverID uuid.UUID) (*models.FriendRequest, error)
	FindPendingRequests(ctx context.Context, userID uuid.UUID) ([]models.FriendRequest, error)
	UpdateRequest(ctx context.Context, req *models.FriendRequest) error

	// Friendships
	CreateFriendship(ctx context.Context, friendship *models.Friendship) error
	DeleteFriendship(ctx context.Context, userA, userB uuid.UUID) error
	ExistsFriendship(ctx context.Context, userA, userB uuid.UUID) (bool, error)
	FindFriends(ctx context.Context, userID uuid.UUID) ([]models.User, error)

	// Blocked Users
	BlockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error
	UnblockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error
	IsBlocked(ctx context.Context, userA, userB uuid.UUID) (bool, error)
	FindBlockedUsers(ctx context.Context, userID uuid.UUID) ([]models.User, error)
}

type friendRepository struct {
	db *gorm.DB
}

func NewFriendRepository(db *gorm.DB) FriendRepository {
	return &friendRepository{db: db}
}

func (r *friendRepository) CreateRequest(ctx context.Context, req *models.FriendRequest) error {
	return r.db.WithContext(ctx).Create(req).Error
}

func (r *friendRepository) FindRequestByID(ctx context.Context, id uuid.UUID) (*models.FriendRequest, error) {
	var req models.FriendRequest
	err := r.db.WithContext(ctx).First(&req, "id = ?", id).Error
	if err != nil {
		return nil, err
	}
	return &req, nil
}

func (r *friendRepository) FindRequestBetween(ctx context.Context, senderID, receiverID uuid.UUID) (*models.FriendRequest, error) {
	var req models.FriendRequest
	err := r.db.WithContext(ctx).Where(
		"((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))",
		senderID, receiverID, receiverID, senderID,
	).First(&req).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}
	return &req, nil
}

func (r *friendRepository) FindPendingRequests(ctx context.Context, userID uuid.UUID) ([]models.FriendRequest, error) {
	var reqs []models.FriendRequest
	err := r.db.WithContext(ctx).Where("receiver_id = ? AND status = ?", userID, models.FriendRequestPending).Order("created_at desc").Find(&reqs).Error
	return reqs, err
}

func (r *friendRepository) UpdateRequest(ctx context.Context, req *models.FriendRequest) error {
	return r.db.WithContext(ctx).Save(req).Error
}

func (r *friendRepository) CreateFriendship(ctx context.Context, friendship *models.Friendship) error {
	return r.db.WithContext(ctx).Create(friendship).Error
}

func (r *friendRepository) DeleteFriendship(ctx context.Context, userA, userB uuid.UUID) error {
	return r.db.WithContext(ctx).Where(
		"((user_a = ? AND user_b = ?) OR (user_a = ? AND user_b = ?))",
		userA, userB, userB, userA,
	).Delete(&models.Friendship{}).Error
}

func (r *friendRepository) ExistsFriendship(ctx context.Context, userA, userB uuid.UUID) (bool, error) {
	var count int64
	err := r.db.WithContext(ctx).Model(&models.Friendship{}).Where(
		"((user_a = ? AND user_b = ?) OR (user_a = ? AND user_b = ?))",
		userA, userB, userB, userA,
	).Count(&count).Error
	return count > 0, err
}

func (r *friendRepository) FindFriends(ctx context.Context, userID uuid.UUID) ([]models.User, error) {
	var friendships []models.Friendship
	err := r.db.WithContext(ctx).Where("user_a = ? OR user_b = ?", userID, userID).Find(&friendships).Error
	if err != nil {
		return nil, err
	}

	if len(friendships) == 0 {
		return []models.User{}, nil
	}

	friendIDs := make([]uuid.UUID, 0, len(friendships))
	for _, f := range friendships {
		if f.UserA == userID {
			friendIDs = append(friendIDs, f.UserB)
		} else {
			friendIDs = append(friendIDs, f.UserA)
		}
	}

	var friends []models.User
	err = r.db.WithContext(ctx).Where("id IN ?", friendIDs).Find(&friends).Error
	if err != nil {
		return nil, err
	}

	return friends, nil
}

func (r *friendRepository) BlockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error {
	block := &models.BlockedUser{
		BlockerID: blockerID,
		BlockedID: blockedID,
	}
	return r.db.WithContext(ctx).Create(block).Error
}

func (r *friendRepository) UnblockUser(ctx context.Context, blockerID, blockedID uuid.UUID) error {
	return r.db.WithContext(ctx).
		Where("blocker_id = ? AND blocked_id = ?", blockerID, blockedID).
		Delete(&models.BlockedUser{}).Error
}

func (r *friendRepository) IsBlocked(ctx context.Context, userA, userB uuid.UUID) (bool, error) {
	var count int64
	err := r.db.WithContext(ctx).Model(&models.BlockedUser{}).
		Where("(blocker_id = ? AND blocked_id = ?) OR (blocker_id = ? AND blocked_id = ?)",
			userA, userB, userB, userA).
		Count(&count).Error
	return count > 0, err
}

func (r *friendRepository) FindBlockedUsers(ctx context.Context, userID uuid.UUID) ([]models.User, error) {
	var blocked []models.BlockedUser
	err := r.db.WithContext(ctx).Where("blocker_id = ?", userID).Find(&blocked).Error
	if err != nil {
		return nil, err
	}

	if len(blocked) == 0 {
		return []models.User{}, nil
	}

	blockedIDs := make([]uuid.UUID, 0, len(blocked))
	for _, b := range blocked {
		blockedIDs = append(blockedIDs, b.BlockedID)
	}

	var users []models.User
	err = r.db.WithContext(ctx).Where("id IN ?", blockedIDs).Find(&users).Error
	if err != nil {
		return nil, err
	}

	return users, nil
}

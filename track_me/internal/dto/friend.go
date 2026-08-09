package dto

type SendFriendRequest struct {
	ReceiverEmail string `json:"receiver_email" validate:"required,email"`
}

type RespondFriendRequest struct {
	RequestID string `json:"request_id" validate:"required,uuid"`
	Accept    bool   `json:"accept"`
}

type FriendRequestResponse struct {
	ID        string       `json:"id"`
	Sender    UserResponse `json:"sender"`
	Receiver  UserResponse `json:"receiver"`
	Status    string       `json:"status"`
	CreatedAt string       `json:"created_at"`
}

package dto

type NotificationResponse struct {
	ID        string `json:"id"`
	UserID    string `json:"user_id"`
	Type      string `json:"type"`
	Title     string `json:"title"`
	Body      string `json:"body"`
	Data      string `json:"data,omitempty"`
	Read      bool   `json:"read"`
	CreatedAt string `json:"created_at"`
}

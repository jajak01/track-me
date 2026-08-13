package dto

type SharingRequest struct {
	FriendID string `json:"friend_id" validate:"required,uuid"`
}

type SharingApproval struct {
	NotificationID string `json:"notification_id" validate:"required,uuid"`
	Approve        bool   `json:"approve"`
}

type LocationUpdateRequest struct {
	Latitude          float64 `json:"latitude" validate:"required,latitude"`
	Longitude         float64 `json:"longitude" validate:"required,longitude"`
	Accuracy          float64 `json:"accuracy"`
	Altitude          float64 `json:"altitude"`
	Bearing           float64 `json:"bearing"`
	Speed             float64 `json:"speed"`
	BatteryPercentage int     `json:"battery_percentage"`
	IsCharging        bool    `json:"is_charging"`
	ActivityType      string  `json:"activity_type"`
	IsMock            bool    `json:"is_mock"`
	Timestamp         string  `json:"timestamp"`
}

type LocationResponse struct {
	UserID            string  `json:"user_id"`
	Latitude          float64 `json:"latitude"`
	Longitude         float64 `json:"longitude"`
	Accuracy          float64 `json:"accuracy"`
	Altitude          float64 `json:"altitude"`
	Bearing           float64 `json:"bearing"`
	Speed             float64 `json:"speed"`
	BatteryPercentage int     `json:"battery_percentage"`
	IsCharging        bool    `json:"is_charging"`
	ActivityType      string  `json:"activity_type"`
	IsMock            bool    `json:"is_mock"`
	Timestamp         string  `json:"timestamp"`
}

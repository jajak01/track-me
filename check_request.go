//go:build ignore

package main

import (
	"fmt"
	"github.com/desen/track-me/internal/config"
	"github.com/desen/track-me/internal/database"
	"github.com/desen/track-me/internal/models"
)

func main() {
	cfg, _ := config.Load()
	db, err := database.Connect(cfg.Database)
	if err != nil {
		fmt.Println("DB connect error:", err)
		return
	}

	var fr models.FriendRequest
	err = db.First(&fr, "id = ?", "7eb7e562-a04d-4cf4-8ffc-6c17135cc305").Error
	if err != nil {
		fmt.Println("Friend request error:", err)
	} else {
		fmt.Printf("Request: id=%s sender=%s receiver=%s status=%s\n", fr.ID, fr.SenderID, fr.ReceiverID, fr.Status)
	}

	var users []models.User
	db.Find(&users)
	for _, u := range users {
		fmt.Printf("User: id=%s email=%s name=%s\n", u.ID, u.Email, u.DisplayName)
	}
}

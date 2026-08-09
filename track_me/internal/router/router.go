package router

import (
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"

	_ "github.com/desen/track-me/docs"
	"github.com/desen/track-me/internal/config"
	"github.com/desen/track-me/internal/handler"
	"github.com/desen/track-me/internal/middleware"
	"github.com/desen/track-me/pkg/jwt"
)

type Router struct {
	cfg         *config.Config
	jwtService  *jwt.Service
	authH       *handler.AuthHandler
	userH       *handler.UserHandler
	friendH     *handler.FriendHandler
	locationH   *handler.LocationHandler
	notifH      *handler.NotificationHandler
	wsH         *handler.WebSocketHandler
}

func NewRouter(
	cfg *config.Config,
	jwtService *jwt.Service,
	authH *handler.AuthHandler,
	userH *handler.UserHandler,
	friendH *handler.FriendHandler,
	locationH *handler.LocationHandler,
	notifH *handler.NotificationHandler,
	wsH *handler.WebSocketHandler,
) *Router {
	return &Router{
		cfg:        cfg,
		jwtService: jwtService,
		authH:      authH,
		userH:      userH,
		friendH:    friendH,
		locationH:  locationH,
		notifH:     notifH,
		wsH:        wsH,
	}
}

func (r *Router) Setup() *gin.Engine {
	// Set Gin mode
	if r.cfg.Environment == "production" {
		gin.SetMode(gin.ReleaseMode)
	} else {
		gin.SetMode(gin.DebugMode)
	}

	app := gin.New()

	// Global Middlewares
	app.Use(gin.Recovery())
	app.Use(middleware.Logger())
	app.Use(middleware.CORS(r.cfg.CORS))
	app.Use(middleware.RateLimit(r.cfg.RateLimit.RequestsPerMinute))

	// Swagger Route
	app.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// WebSocket Route
	app.GET("/ws", r.wsH.ServeWs)

	// API Group
	api := app.Group("/api/v1")
	{
		// Public Auth Routes
		auth := api.Group("/auth")
		{
			auth.POST("/register", r.authH.Register)
			auth.POST("/login", r.authH.Login)
			auth.POST("/refresh", r.authH.RefreshToken)
			auth.POST("/logout", r.authH.Logout) // requires refresh token in body
		}

		// Protected Routes
		protected := api.Group("")
		protected.Use(middleware.Auth(r.jwtService))
		{
			// Auth changing/deleting
			protected.POST("/auth/change-password", r.authH.ChangePassword)
			protected.DELETE("/auth/delete", r.authH.DeleteAccount)

			// User Profile
			user := protected.Group("/user")
			{
				user.GET("/profile", r.userH.GetProfile)
				user.PATCH("/profile", r.userH.UpdateProfile)
			}

			// Friend System
			friend := protected.Group("/friend")
			{
				friend.POST("/request", r.friendH.SendFriendRequest)
				friend.POST("/respond", r.friendH.RespondFriendRequest)
				friend.GET("/requests", r.friendH.GetPendingRequests)
				friend.GET("/list", r.friendH.GetFriends)
				friend.DELETE("/remove/:id", r.friendH.RemoveFriend)
			}

			// Sharing Location Permissions
			sharing := protected.Group("/sharing")
			{
				sharing.POST("/request", r.locationH.RequestSharing)
				sharing.POST("/approve", r.locationH.ApproveSharing)
				sharing.DELETE("/revoke/:id", r.locationH.RevokeSharing)
			}

			// Locations
			location := protected.Group("/location")
			{
				location.POST("/update", r.locationH.UpdateLocation)
				location.GET("/current/:userId", r.locationH.GetCurrentLocation)
				location.GET("/history/:userId", r.locationH.GetLocationHistory)
			}

			// Notifications
			notification := protected.Group("/notification")
			{
				notification.GET("/list", r.notifH.GetNotifications)
				notification.PATCH("/read/:id", r.notifH.MarkAsRead)
				notification.POST("/read-all", r.notifH.MarkAllAsRead)
			}
		}
	}

	return app
}

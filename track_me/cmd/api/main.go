package main

import (
	"context"
	"errors"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/rs/zerolog/log"

	"github.com/desen/track-me/internal/config"
	"github.com/desen/track-me/internal/database"
	"github.com/desen/track-me/internal/handler"
	"github.com/desen/track-me/internal/repository"
	"github.com/desen/track-me/internal/router"
	"github.com/desen/track-me/internal/service"
	ws "github.com/desen/track-me/internal/websocket"
	"github.com/desen/track-me/pkg/jwt"
	"github.com/desen/track-me/pkg/logger"
	"github.com/desen/track-me/pkg/validator"
)

// @title Mutual Location Sharing API
// @version 1.0
// @description Backend REST & WebSocket API for lightweight, privacy-focused mutual location sharing.
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.swagger.io/support
// @contact.email support@swagger.io

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8080
// @BasePath /
// @query.collection.format multi

// @securityDefinitions.apiKey BearerAuth
// @in header
// @name Authorization
// @description Type "Bearer" followed by a space and JWT token (e.g. Bearer <token>)
func main() {
	// 1. Load config
	cfg, err := config.Load()
	if err != nil {
		log.Fatal().Err(err).Msg("failed to load configuration")
	}

	// 2. Initialize Logger
	logger.Init(cfg.Log.Level, cfg.Log.Pretty)
	log.Info().Msg("logger initialized")

	// 3. Initialize Validator
	validator.Init()
	log.Info().Msg("validator initialized")

	// 4. Connect to database
	db, err := database.Connect(cfg.Database)
	if err != nil {
		log.Fatal().Err(err).Msg("failed to connect to database")
	}

	// 5. Run Database Migrations
	log.Info().Msg("running database migrations...")
	if err := database.RunMigrations(cfg.Database); err != nil {
		log.Fatal().Err(err).Msg("failed to run database migrations")
	}

	// 6. Initialize JWT Service
	jwtService := jwt.NewService(
		cfg.JWT.AccessSecret,
		cfg.JWT.RefreshSecret,
		cfg.JWT.AccessExpiry,
		cfg.JWT.RefreshExpiry,
	)

	// 7. Initialize Repositories
	userRepo := repository.NewUserRepository(db)
	refreshRepo := repository.NewRefreshTokenRepository(db)
	sessionRepo := repository.NewSessionRepository(db)
	auditRepo := repository.NewAuditLogRepository(db)
	friendRepo := repository.NewFriendRepository(db)
	locationRepo := repository.NewLocationRepository(db)
	notifRepo := repository.NewNotificationRepository(db)

	// 8. Initialize Realtime WebSocket Hub
	wsHub := ws.NewHub()
	go wsHub.Run()
	log.Info().Msg("websocket hub started")

	// 9. Initialize Services
	authService := service.NewAuthService(userRepo, refreshRepo, sessionRepo, auditRepo, jwtService)
	userService := service.NewUserService(userRepo)
	friendService := service.NewFriendService(db, friendRepo, userRepo, locationRepo, notifRepo)
	locationService := service.NewLocationService(db, locationRepo, friendRepo, userRepo, notifRepo)
	notifService := service.NewNotificationService(notifRepo)

	// 10. Initialize Handlers
	authH := handler.NewAuthHandler(authService)
	userH := handler.NewUserHandler(userService)
	friendH := handler.NewFriendHandler(friendService)
	locationH := handler.NewLocationHandler(locationService, friendService, wsHub)
	notifH := handler.NewNotificationHandler(notifService)
	wsH := handler.NewWebSocketHandler(wsHub, jwtService)

	// 11. Setup Router
	r := router.NewRouter(cfg, jwtService, authH, userH, friendH, locationH, notifH, wsH)
	engine := r.Setup()

	// 12. Create HTTP Server
	server := &http.Server{
		Addr:    ":" + cfg.Port,
		Handler: engine,
	}

	// Start server in goroutine
	go func() {
		log.Info().Str("port", cfg.Port).Msg("starting HTTP server")
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatal().Err(err).Msg("server ListenAndServe failed")
		}
	}()

	// 13. Graceful Shutdown
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Info().Msg("shutting down HTTP server gracefully...")

	// Context with timeout for server shutdown
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(ctx); err != nil {
		log.Fatal().Err(err).Msg("server forced to shutdown")
	}

	// Close database connection
	sqlDB, err := db.DB()
	if err == nil {
		log.Info().Msg("closing database connection...")
		_ = sqlDB.Close()
	}

	log.Info().Msg("server stopped cleanly")
}

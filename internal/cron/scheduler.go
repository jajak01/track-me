package cron

import (
	"context"

	"github.com/robfig/cron/v3"
	"github.com/rs/zerolog/log"

	"github.com/desen/track-me/internal/repository"
)

// Scheduler wraps the cron scheduler for background tasks
type Scheduler struct {
	cron        *cron.Cron
	refreshRepo repository.RefreshTokenRepository
}

// NewScheduler creates a new cron scheduler with periodic cleanup jobs
func NewScheduler(refreshRepo repository.RefreshTokenRepository) *Scheduler {
	return &Scheduler{
		cron:        cron.New(),
		refreshRepo: refreshRepo,
	}
}

// Start registers and starts all cron jobs
func (s *Scheduler) Start() {
	// Clean up expired refresh tokens every hour
	_, err := s.cron.AddFunc("@every 1h", func() {
		log.Debug().Msg("cron: cleaning up expired refresh tokens")
		if err := s.refreshRepo.DeleteExpired(context.Background()); err != nil {
			log.Error().Err(err).Msg("cron: failed to clean up expired refresh tokens")
		}
	})
	if err != nil {
		log.Error().Err(err).Msg("cron: failed to register refresh token cleanup job")
	}

	s.cron.Start()
	log.Info().Msg("cron scheduler started")
}

// Stop gracefully stops the cron scheduler
func (s *Scheduler) Stop() {
	ctx := s.cron.Stop()
	<-ctx.Done()
	log.Info().Msg("cron scheduler stopped")
}

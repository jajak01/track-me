package config

import (
	"os"
	"strconv"
	"time"

	"github.com/joho/godotenv"
)

type Config struct {
	Port        string
	Environment string
	Database    DatabaseConfig
	JWT         JWTConfig
	Log         LogConfig
	CORS        CORSConfig
	RateLimit   RateLimitConfig
}

type DatabaseConfig struct {
	Host            string
	Port            string
	User            string
	Password        string
	Name            string
	SSLMode         string
	MaxOpenConns    int
	MaxIdleConns    int
	ConnMaxLifetime time.Duration
}

type JWTConfig struct {
	AccessSecret  string
	RefreshSecret string
	AccessExpiry  time.Duration
	RefreshExpiry time.Duration
}

type LogConfig struct {
	Level  string
	Pretty bool
}

type CORSConfig struct {
	AllowedOrigins []string
	AllowedMethods []string
	AllowedHeaders []string
}

type RateLimitConfig struct {
	RequestsPerMinute int
}

func Load(path ...string) (*Config, error) {
	p := ".env"
	if len(path) > 0 && path[0] != "" {
		p = path[0]
	}
	_ = godotenv.Load(p)

	cfg := &Config{
		Port:        getEnv("PORT", "8080"),
		Environment: getEnv("ENVIRONMENT", "development"),
		Database: DatabaseConfig{
			Host:            getEnv("DB_HOST", "localhost"),
			Port:            getEnv("DB_PORT", "5432"),
			User:            getEnv("DB_USER", "trackme"),
			Password:        getEnv("DB_PASSWORD", "trackme"),
			Name:            getEnv("DB_NAME", "trackme"),
			SSLMode:         getEnv("DB_SSLMODE", "disable"),
			MaxOpenConns:    getEnvInt("DB_MAX_OPEN_CONNS", 25),
			MaxIdleConns:    getEnvInt("DB_MAX_IDLE_CONNS", 10),
			ConnMaxLifetime: time.Duration(getEnvInt("DB_CONN_MAX_LIFETIME_MIN", 5)) * time.Minute,
		},
		JWT: JWTConfig{
			AccessSecret:  getEnv("JWT_ACCESS_SECRET", "change-me-access-secret"),
			RefreshSecret: getEnv("JWT_REFRESH_SECRET", "change-me-refresh-secret"),
			AccessExpiry:  time.Duration(getEnvInt("JWT_ACCESS_EXPIRY_MIN", 15)) * time.Minute,
			RefreshExpiry: time.Duration(getEnvInt("JWT_REFRESH_EXPIRY_DAYS", 365)) * 24 * time.Hour,
		},
		Log: LogConfig{
			Level:  getEnv("LOG_LEVEL", "info"),
			Pretty: getEnv("LOG_PRETTY", "true") == "true",
		},
		CORS: CORSConfig{
			AllowedOrigins: []string{"*"},
			AllowedMethods: []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"},
			AllowedHeaders: []string{"Origin", "Content-Type", "Accept", "Authorization"},
		},
		RateLimit: RateLimitConfig{
			RequestsPerMinute: getEnvInt("RATE_LIMIT_RPM", 60),
		},
	}

	return cfg, nil
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func getEnvInt(key string, fallback int) int {
	v := os.Getenv(key)
	if v == "" {
		return fallback
	}
	i, err := strconv.Atoi(v)
	if err != nil {
		return fallback
	}
	return i
}

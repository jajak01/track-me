# track-me — Comprehensive Codebase Context Cache

> Auto-generated analysis. Load this instead of re-reading every file.

---

## PROJECT OVERVIEW

**track-me** is a privacy-focused mutual location sharing backend written in Go. It serves an Android frontend (Kotlin/Jetpack Compose) and enforces that location sharing only works when *both users explicitly consent* — one requests, the other approves.

- **Module:** `github.com/desen/track-me`
- **Go version:** 1.26.3
- **Framework:** Gin
- **ORM:** GORM (PostgreSQL via `pgx`)
- **Auth:** JWT (access + refresh token pair), bcrypt
- **Realtime:** Gorilla WebSocket
- **Migrations:** golang-migrate
- **Logging:** zerolog
- **Docs:** Swagger (`/swagger/*any`)
- **Dep:** Go modules, Dockerized

---

## FILE TREE

```
C:\Users\desen\track_me\
├── cmd/api/main.go              # Entry point — wires everything, starts HTTP server, graceful shutdown
├── internal/
│   ├── config/config.go         # Env-based config (PORT, DB, JWT, CORS, RateLimit)
│   ├── database/
│   │   ├── database.go          # PostgreSQL connection via GORM (SkipDefaultTransaction, PrepareStmt)
│   │   └── migrate.go           # Runs file://migrations against postgres
│   ├── models/                  # GORM models (all in one file social.go plus separate files)
│   │   ├── user.go              # User (UUID, email, password_hash, display_name, avatar, phone, status_message)
│   │   ├── social.go            # FriendRequest, Friendship, BlockedUser, LocationPermission, Location, Device, Notification
│   │   ├── session.go           # Session (user_id, refresh_token, ip, ua, expires)
│   │   ├── refresh_token.go     # RefreshToken (user_id, token, expires)
│   │   └── audit_log.go         # AuditLog (user_id, action, ip, ua, details)
│   ├── dto/                     # Request/Response structs with validation tags
│   │   ├── auth.go              # RegisterRequest, LoginRequest, RefreshRequest, ChangePasswordRequest, LoginResponse, UserResponse, UpdateProfileRequest
│   │   ├── friend.go            # SendFriendRequest, RespondFriendRequest, FriendRequestResponse, BlockUserRequest
│   │   ├── sharing.go           # SharingRequest, SharingApproval, LocationUpdateRequest, LocationResponse
│   │   ├── notification.go      # NotificationResponse
│   │   └── response.go          # APIResponse{success,message,data,errors}, PaginatedResponse
│   ├── repository/              # Data access layer — each repo is an interface + GORM implementation
│   │   ├── user_repo.go         # Create, FindByID, FindByEmail, Update, Delete, ExistsByEmail
│   │   ├── friend_repo.go       # FriendRequest CRUD, Friendship CRUD, Block/Unblock/IsBlocked
│   │   ├── location_repo.go     # LocationPermission CRUD, Location CRUD, GetLatestLocation, GetLatestLocations (subquery), GetLocationHistory, DeleteLocationHistory
│   │   ├── refresh_token_repo.go
│   │   ├── session_repo.go
│   │   ├── notification_repo.go
│   │   ├── audit_log_repo.go
│   │   └── device_repo.go
│   ├── service/                 # Business logic — each service is an interface + implementation
│   │   ├── auth_service.go      # Register, Login (bcrypt+JWT+session+audit), RefreshToken (rotate), Logout, ChangePassword (invalidate all), DeleteAccount (soft-delete)
│   │   ├── friend_service.go    # SendRequest (by email), RespondRequest (accept→create friendship+notify), GetPending, GetFriends, RemoveFriend (revoke sharing), Block/Unblock
│   │   ├── location_service.go  # RequestSharing (verify friends→create notification), ApproveSharing (transaction: grant BOTH directions→mutual), RevokeSharing (revoke both→notify), UpdateLocation, GetCurrentLocation (verify friends+sharing active), GetLocationHistory, IsSharingActive (checks both directions granted)
│   │   ├── notification_service.go
│   │   └── user_service.go
│   ├── handler/                 # HTTP handlers — thin, delegate to services, Swagger annotations
│   │   ├── auth_handler.go      # POST /api/v1/auth/{register,login,refresh,logout,change-password}, DELETE /api/v1/auth/delete
│   │   ├── friend_handler.go    # POST /api/v1/friend/{request,respond,block}, GET {requests,list,blocked}, DELETE {remove/:id,unblock/:id}
│   │   ├── location_handler.go  # POST /api/v1/sharing/{request,approve}, DELETE /api/v1/sharing/revoke/:id, POST /api/v1/location/update, GET /api/v1/location/{current/:userId,history/:userId}
│   │   ├── notification_handler.go
│   │   ├── user_handler.go
│   │   └── websocket_handler.go # GET /ws?token=... — authenticates via JWT, upgrades to WebSocket
│   ├── middleware/
│   │   ├── auth.go              # Extracts Bearer token, validates, sets userID+email in context
│   │   ├── cors.go              # Per-origin CORS, handles OPTIONS preflight
│   │   ├── security.go          # Helmet-like: X-Content-Type-Options, X-Frame-Options, X-XSS-Protection, HSTS, Permissions-Policy, Cache-Control
│   │   ├── rate_limit.go        # Per-IP token bucket (golang.org/x/time/rate), cleanup goroutine every 5min
│   │   └── logger.go            # Structured request logging with zerolog (method, path, status, latency, ip)
│   ├── router/router.go         # Wires all routes: global middleware, /swagger, /ws, /api/v1/{auth(public), protected}
│   ├── websocket/hub.go         # Hub pattern: register/unregister/broadcast channels, per-user client map, SendToUser method, ReadPump/WritePump with ping/pong, heartbeat
│   └── cron/scheduler.go        # Hourly cleanup of expired refresh tokens via robfig/cron
├── pkg/
│   ├── jwt/jwt.go               # HS256 JWT: GenerateTokenPair, ValidateAccessToken, ValidateRefreshToken, Claims{UserID,Email}
│   ├── logger/logger.go         # zerolog Init with level and pretty/JSON output
│   └── validator/validator.go   # go-playground/validator wrapper with human-readable error formatting
├── migrations/
│   ├── 000001_init_schema.up.sql   # 10 tables: users, refresh_tokens, sessions, audit_logs, friend_requests, friendships, location_permissions, locations, devices, notifications, blocked_users
│   └── 000001_init_schema.down.sql
├── docs/                        # Swagger spec (swagger.json, swagger.yaml, docs.go)
├── Dockerfile                   # Multi-stage: golang:1.26-alpine builder → alpine:3.19 runtime
├── .env / .env.example
├── go.mod / go.sum
└── plan.txt                     # Full project specification (features, phases, tech stack, API design, DB schema)
```

---

## DATABASE SCHEMA (10 tables)

| Table | Key Columns | Notes |
|-------|-------------|-------|
| `users` | id(UUID PK), email(UNIQUE), password_hash, display_name, avatar, phone, status_message, deleted_at | GORM soft-delete |
| `refresh_tokens` | id, user_id(FK), token(UNIQUE), expires_at | Indexed |
| `sessions` | id, user_id(FK), refresh_token, ip_address, user_agent, expires_at | |
| `audit_logs` | id, user_id(FK), action(varchar 50), ip_address, user_agent, details | |
| `friend_requests` | id, sender_id(FK), receiver_id(FK), status(pending/accepted/rejected), UNIQUE(sender,receiver) | |
| `friendships` | id, user_a(FK), user_b(FK), UNIQUE(user_a,user_b) | Order-agnostic queries |
| `location_permissions` | id, owner_id(FK), viewer_id(FK), status(granted/revoked), granted_at, revoked_at, UNIQUE(owner,viewer) | |
| `locations` | id, user_id(FK), lat, lng, accuracy, altitude, bearing, speed, battery, charging, activity, mock_location, gps_provider, timestamp | Composite index (user_id,timestamp DESC) |
| `devices` | id, user_id(FK), device_name, platform, fcm_token, last_seen_at | |
| `notifications` | id, user_id(FK), type(varchar 30), title, body, data(JSONB), read(bool) | |
| `blocked_users` | id, blocker_id(FK), blocked_id(FK), UNIQUE(blocker,blocked) | |

---

## API ENDPOINTS

### Public (no auth):
| Method | Path | Handler | Description |
|--------|------|---------|-------------|
| POST | `/api/v1/auth/register` | `authH.Register` | Create account |
| POST | `/api/v1/auth/login` | `authH.Login` | Login → token pair |
| POST | `/api/v1/auth/refresh` | `authH.RefreshToken` | Rotate tokens |
| POST | `/api/v1/auth/logout` | `authH.Logout` | Invalidate session |
| GET | `/ws` | `wsH.ServeWs` | WebSocket (token via query param or header) |
| GET | `/swagger/*any` | Swagger UI | API docs |

### Protected (Bearer token required):
| Method | Path | Handler | Description |
|--------|------|---------|-------------|
| POST | `/api/v1/auth/change-password` | `authH.ChangePassword` | Change password (invalidates all sessions) |
| DELETE | `/api/v1/auth/delete` | `authH.DeleteAccount` | Soft-delete account |
| GET | `/api/v1/user/profile` | `userH.GetProfile` | Get own profile |
| PATCH | `/api/v1/user/profile` | `userH.UpdateProfile` | Update display name, avatar, phone, status |
| POST | `/api/v1/friend/request` | `friendH.SendFriendRequest` | Send friend request by email |
| POST | `/api/v1/friend/respond` | `friendH.RespondFriendRequest` | Accept/reject |
| GET | `/api/v1/friend/requests` | `friendH.GetPendingRequests` | Incoming pending |
| GET | `/api/v1/friend/list` | `friendH.GetFriends` | Friend list |
| DELETE | `/api/v1/friend/remove/:id` | `friendH.RemoveFriend` | Unfriend + revoke sharing |
| POST | `/api/v1/friend/block` | `friendH.BlockUser` | Block + remove friend + revoke |
| DELETE | `/api/v1/friend/unblock/:id` | `friendH.UnblockUser` | Unblock |
| GET | `/api/v1/friend/blocked` | `friendH.GetBlockedUsers` | Blocked list |
| POST | `/api/v1/sharing/request` | `locationH.RequestSharing` | Request location sharing with friend |
| POST | `/api/v1/sharing/approve` | `locationH.ApproveSharing` | Approve/deny via notification ID |
| DELETE | `/api/v1/sharing/revoke/:id` | `locationH.RevokeSharing` | Revoke sharing (both directions) |
| POST | `/api/v1/location/update` | `locationH.UpdateLocation` | Post GPS update → broadcast via WS |
| GET | `/api/v1/location/current/:userId` | `locationH.GetCurrentLocation` | Get friend's latest location |
| GET | `/api/v1/location/history/:userId` | `locationH.GetLocationHistory` | Get friend's history (start/end query params) |
| GET | `/api/v1/notification/list` | `notifH.GetNotifications` | List notifications |
| PATCH | `/api/v1/notification/read/:id` | `notifH.MarkAsRead` | Mark one as read |
| POST | `/api/v1/notification/read-all` | `notifH.MarkAllAsRead` | Mark all as read |

---

## WEBSOCKET EVENTS

Hub pattern: per-user `map[*Client]bool`, buffered channels (256).

| Event | Direction | Purpose |
|-------|-----------|---------|
| `heartbeat` | Client→Server | Pong response `{"event":"heartbeat","data":"pong"}` |
| `location:update` | Server→Client | Broadcast to sharing friends when location updated |
| `notification` | Server→Client | Push notification (friend request, sharing request, etc.) |
| `sharing:update` | Server→Client | Revoke notification |
| `friend:update` | Server→Client | Friend status changes |

Read limit: 512KB. Ping/pong keepalive: 54s/60s. Write timeout: 10s.

---

## KEY ARCHITECTURE PATTERNS

1. **Clean Architecture**: handler → service (interface) → repository (interface) → GORM/DB
2. **Dependency Injection**: Manual wiring in `main.go` — no DI framework
3. **Repository Pattern**: Every data access behind an interface (testable/mockable)
4. **Mutual Consent**: Location sharing requires BOTH directions granted (`IsSharingActive` checks both)
5. **Token Rotation**: Refresh token rotated on every use (old deleted, new issued)
6. **Transactions**: Friend accept, sharing approve, revoke, remove friend all use `db.Transaction`
7. **Soft Delete**: Users use GORM `DeletedAt`; locations/referenced data cascade
8. **Audit Trail**: Every auth action logged to `audit_logs`

---

## CONFIGURATION (.env)

| Key | Default | Description |
|-----|---------|-------------|
| PORT | 8080 | HTTP port |
| ENVIRONMENT | development | `production` → ReleaseMode, otherwise DebugMode |
| DB_HOST/PORT/USER/PASSWORD/NAME/SSLMODE | localhost:5432/trackme | PostgreSQL |
| DB_MAX_OPEN_CONNS/IDLE_CONNS | 25/10 | Connection pool |
| DB_CONN_MAX_LIFETIME_MIN | 5 | Conn max lifetime |
| JWT_ACCESS_SECRET/REFRESH_SECRET | (change-me defaults) | HS256 signing keys |
| JWT_ACCESS_EXPIRY_MIN | 15 | Access token TTL |
| JWT_REFRESH_EXPIRY_DAYS | 7 | Refresh token TTL |
| LOG_LEVEL/LOG_PRETTY | info/true | zerolog config |
| RATE_LIMIT_RPM | 60 | Per-IP requests per minute |

---

## KEY DEPENDENCIES

| Package | Purpose |
|---------|---------|
| `github.com/gin-gonic/gin` | HTTP framework |
| `gorm.io/gorm` + `gorm.io/driver/postgres` | ORM |
| `github.com/golang-jwt/jwt/v5` | JWT HS256 |
| `golang.org/x/crypto` | bcrypt |
| `github.com/gorilla/websocket` | WebSocket |
| `github.com/google/uuid` | UUID generation |
| `github.com/golang-migrate/migrate/v4` | DB migrations |
| `github.com/robfig/cron/v3` | Scheduled cleanup |
| `github.com/rs/zerolog` | Structured logging |
| `github.com/go-playground/validator/v10` | Input validation |
| `github.com/joho/godotenv` | .env loading |
| `github.com/swaggo/*` | Swagger docs |
| `golang.org/x/time/rate` | Token bucket rate limiter |

---

## NOTABLE PATTERNS & QUIRKS

- **Duplicate codebase**: `track_me/track_me/` exists — likely a copy; the canonical code is at `track_me/` root.
- **Empty packages**: `internal/auth/`, `internal/friend/`, `internal/location/`, `internal/user/`, `internal/notification/` exist as directories but contain no Go files (logic lives in service/handler/repository instead).
- **Global DB var**: `database.DB` is set in `Connect()` — used sparingly, most code uses injected `*gorm.DB`.
- **Sharing mutual by default**: When B approves A's sharing request, `ApproveSharing` grants BOTH `A→B` AND `B→A` permissions in a single transaction, making tracking fully mutual immediately.
- **Revoke is nuclear**: Both `RevokeSharing` and `RemoveFriend` revoke permissions in BOTH directions.
- **Rate limiter cleanup**: A background goroutine cleans stale per-IP limiters every 5 minutes.
- **Repository constructors accept `*gorm.DB`**: This allows reusing repositories within transactions (e.g. `repository.NewFriendRepository(tx)`).

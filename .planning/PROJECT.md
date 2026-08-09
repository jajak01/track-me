# Track Me

## What This Is

A privacy-focused mutual location sharing app. Users connect with friends, both explicitly consent to location sharing, then see each other's real-time GPS position on a map. Backend in Go (Gin/GORM/PostgreSQL), Android app in Kotlin (Jetpack Compose/MapLibre).

## Core Value

**Mutual consent location sharing that actually works end-to-end** — if either user revokes, tracking stops immediately. Privacy by design, not as an afterthought.

## Requirements

### Validated

- ✓ Backend: REST API with 25 endpoints (auth, friends, sharing, location, notifications) — tested, all 200/201 responses verified
- ✓ Backend: WebSocket hub with per-user client routing, heartbeat, ping/pong keepalive
- ✓ Backend: JWT access+refresh token pair with rotation and auto-cleanup
- ✓ Backend: PostgreSQL schema (10 tables) with migrations, indexes, FK constraints
- ✓ Backend: Rate limiting, security headers, CORS, audit logging
- ✓ Backend: Swagger API documentation
- ✓ Android: Project scaffold with Gradle, Hilt DI, Retrofit, MapLibre, Compose (28 files)
- ✓ Android: MVVM architecture with sealed Result type, DataStore token persistence
- ✓ Android: Auth screens (Login, Register), Friends screen, Notifications screen, Profile screen
- ✓ Android: MapScreen with MapLibre AndroidView and Carto positron tiles

### Active

- [ ] User can open Android project in Android Studio and build successfully
- [ ] User can register and login from Android app against real backend
- [ ] User can send and accept friend requests from Android
- [ ] User can request, approve, and revoke location sharing from Android
- [ ] User sees friends as live-updating markers on the map via WebSocket
- [ ] User's own location is posted to backend and visible to sharing friends
- [ ] Notifications appear in-app and can be acted on (approve sharing, mark read)
- [ ] User can view and edit their profile, and logout

### Out of Scope

- Push notifications (FCM) — WebSocket covers real-time when app is open
- Offline map tiles (MBTiles) — future optimization
- Geofencing / arrival alerts — future feature
- iOS app — Android first
- Google Maps integration — OpenStreetMap is free and sufficient

## Current Milestone: v1.0 End-to-End Live Tracking

**Goal:** Android app builds and runs, all screens functional against the real backend, with live moving friend markers on the map via WebSocket.

**Target features:**
- Buildable Android project (fix any Gradle/compilation issues)
- Working auth flow (register → login → token persistence)
- Friend management (add by email, accept/reject, remove, block)
- Location sharing flow (request → approve → mutual tracking → revoke)
- Live map with real-time moving markers for sharing friends
- Notifications with inline sharing approval
- Profile with edit and logout

## Context

- **Backend:** Go 1.26, Gin, GORM, PostgreSQL, JWT HS256, Gorilla WebSocket, zerolog. All 25 endpoints tested (REASONIX_CONTEXT.md has full detail).
- **Backend already broadcasts:** When a user posts a location update, the handler broadcasts it via WebSocket to all sharing friends who are connected.
- **Android scaffold:** 28 Kotlin files, Compose with Material3, MapLibre 11.7.0, Retrofit 2.11.0 with Kotlinx Serialization, Hilt 2.53.1, OkHttp 4.12.0 with auto-refresh authenticator. `10.0.2.2:8080` for emulator → host localhost.
- **Key risk:** Android project has never been built — Gradle sync, dependency resolution, and compilation issues are expected.
- **MapLibre challenge:** MapLibre Android SDK v11.7.0 requires proper initialization and license key configuration. The current scaffold uses a free Carto tile style URL.

## Constraints

- **Tech stack:** Android must stay Kotlin + Jetpack Compose + MapLibre
- **Map:** OpenStreetMap tiles only (no Google Maps billing)
- **Backend:** No changes to the Go backend unless a gap is found during Android integration
- **Dev environment:** Windows, Android emulator, backend on localhost:8080

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Go backend with Gin + GORM | Fast, idiomatic, good PostgreSQL support | ✓ Good |
| JWT with refresh token rotation | Stateless auth, secure token lifecycle | ✓ Good |
| OpenStreetMap + MapLibre | Completely free, no API keys, self-hostable | ✓ Good |
| WebSocket for real-time | Low latency, persistent connection, already implemented | ✓ Good |
| Hilt for Android DI | Official, compile-time, well-integrated with Compose Navigation | — Pending |

---
*Last updated: 2026-08-08 after Milestone v1.0 kickoff*

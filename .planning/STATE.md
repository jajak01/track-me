# STATE.md — Track Me

## Project Reference

See: .planning/PROJECT.md (updated 2026-08-08)

**Core value:** Mutual consent location sharing that actually works end-to-end
**Current focus:** v1.0 — Android build + live tracking

## Current Position

Phase: Not started (defining requirements)
Plan: —
Status: Defining requirements
Last activity: 2026-08-08 — Milestone v1.0 started

## Accumulated Context

- Backend is fully tested (25 endpoints, all return correct HTTP codes)
- Android project scaffolded with 28 files but never built
- Backend already broadcasts location updates via WebSocket to connected sharing friends
- Notification data fix applied (empty JSONB → "{}" via BeforeCreate hook)
- Auth middleware accepts both raw tokens and "Bearer <token>" format
- Test users: reza01@gmail.com / reza02@gmail.com (pw: reza1234), already friends

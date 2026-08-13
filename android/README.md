# TrackMe — Android (Headless / Background-First)

This is the mobile client from `plan.txt` v2.0. It has **no map** — it is a
background GPS recorder:

1. **One-time register / login** — a minimal Compose screen.
2. **Login forever** — the JWT pair (access + refresh token) is persisted in
   Jetpack DataStore and silently re-authenticated (OkHttp `Authenticator`) and
   auto-restarted on boot.
3. **Background recording** — a `ForegroundService` reads `FusedLocationProviderClient`
   fixes and `POST`s them to `POST /api/v1/location/update`.
4. **Offline queue** — failed posts are stored in a JSON file and flushed by a
   `WorkManager` worker when the network returns.

The phone never renders coordinates; it only produces them and hits the API.

## Project layout

```
app/src/main/java/com/desen/trackme/
├── core/
│   ├── di/AppModule.kt            # Hilt: Retrofit, OkHttp, DataStore, FusedLocation
│   ├── datastore/SessionDataStore.kt
│   ├── session/Session.kt, TokenStore.kt
│   └── network/                   # ApiService, RefreshApiService, interceptors, DTOs
├── data/                          # AuthRepositoryImpl, LocationRepositoryImpl, PendingLocationStore
├── domain/repository/             # AuthRepository, LocationRepository (interfaces)
├── feature/auth/                  # Login/Register Compose screen + ViewModel
├── feature/status/                # Status dashboard (toggle + logout)
├── service/                       # LocationService (ForegroundService), BootReceiver, SyncWorker
├── ui/                            # AppRoot navigation + theme
├── MainActivity.kt
└── TrackMeApp.kt
```

## Build

Requires JDK 17 and the Android SDK (API 34). From this `android/` directory:

```bash
# Linux/macOS (and Git Bash on Windows)
./gradlew assembleDebug

# If the Gradle wrapper jar is not present yet, generate it once from a machine with Gradle:
gradle wrapper --gradle-version 8.6
```

Open the `android/` folder in Android Studio and let it sync, then run on an
emulator or device.

## Pointing at the backend

The base URL defaults to `http://10.0.2.2:8080/` (the Android emulator's alias
for the host machine, where the Go backend runs on port 8080).

For a **physical device**, use your computer's LAN IP:

```bash
./gradlew assembleDebug -PbaseUrl=http://192.168.1.20:8080/
```

Cleartext HTTP is enabled for development (`android:usesCleartextTraffic="true"`);
switch to HTTPS + a reverse proxy (Nginx) for production.

## Runtime permissions

On first launch the app requests `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`
and (Android 13+) `POST_NOTIFICATIONS`. The foreground service needs
`FOREGROUND_SERVICE_LOCATION`, which is declared in the manifest.

## What it does NOT do (by design)

- No map / tile rendering / MapLibre / OpenStreetMap SDK.
- No friend list UI yet — the backend friend & sharing APIs still exist and a
  text-only friend list can be added later (plan.txt Phase 4).
- No WebSocket client — the phone only produces data; dashboards consume via `/ws`.

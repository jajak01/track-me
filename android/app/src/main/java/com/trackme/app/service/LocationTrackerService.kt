package com.trackme.app.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.trackme.app.MainActivity
import com.trackme.app.R
import com.trackme.app.TrackMeApp
import com.trackme.app.data.api.WebSocketManager
import com.trackme.app.data.api.WsEvent
import com.trackme.app.data.local.TokenManager
import com.trackme.app.data.location.LocationClient
import com.trackme.app.data.location.LocationData
import com.trackme.app.data.model.LocationUpdateRequest
import com.trackme.app.data.repository.LocationRepository
import com.trackme.app.data.repository.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackerService : Service() {

    companion object {
        private const val TAG = "LocationTrackerService"
        private const val LOCATION_INTERVAL_MS = 10_000L
        private const val WAKELOCK_TAG = "trackme:location_wakelock"
        private const val WAKELOCK_TIMEOUT_MS = 60_000L // 1 minute max per acquisition

        fun start(context: Context) {
            val intent = Intent(context, LocationTrackerService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationTrackerService::class.java)
            context.stopService(intent)
        }
    }

    @Inject lateinit var locationClient: LocationClient
    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var wsManager: WebSocketManager
    @Inject lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var trackingJob: Job? = null
    private var wsJob: Job? = null
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKELOCK_TAG
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service starting")

        val notification = NotificationCompat.Builder(this, TrackMeApp.LOCATION_CHANNEL_ID)
            .setContentTitle("Track Me")
            .setContentText("Sharing your location in background")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        startForeground(TrackMeApp.LOCATION_NOTIFICATION_ID, notification)

        // Start location tracking
        startLocationTracking()

        // Connect WebSocket for real-time updates
        connectWebSocket()

        return START_STICKY
    }

    private fun startLocationTracking() {
        if (trackingJob?.isActive == true) return

        trackingJob = serviceScope.launch {
            Log.d(TAG, "Location tracking started")
            try {
                // Acquire wake lock during active tracking loop
                wakeLock.acquire(WAKELOCK_TIMEOUT_MS)

                locationClient.getLocationUpdates(LOCATION_INTERVAL_MS).collect { location ->
                    // Re-acquire wakelock on each location to keep CPU awake
                    if (!wakeLock.isHeld) {
                        wakeLock.acquire(WAKELOCK_TIMEOUT_MS)
                    }
                    sendLocationToServer(location)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Location tracking error: ${e.message}", e)
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
                Log.d(TAG, "Location tracking stopped")
            }
        }
    }

    private suspend fun sendLocationToServer(location: LocationData) {
        val token = tokenManager.getAccessToken()
        if (token == null) {
            Log.w(TAG, "No access token, skipping location update")
            return
        }

        val request = LocationUpdateRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy.toDouble(),
            altitude = location.altitude,
            bearing = location.bearing.toDouble(),
            speed = location.speed.toDouble(),
            battery = 0,
            activity = "unknown",
            gpsProvider = location.provider
        )

        when (val result = locationRepository.updateLocation(request)) {
            is Result.Success -> Log.d(TAG, "Location sent: ${location.latitude}, ${location.longitude}")
            is Result.Error -> Log.w(TAG, "Failed to send location: ${result.message}")
        }
    }

    private fun connectWebSocket() {
        if (wsJob?.isActive == true) return

        wsJob = serviceScope.launch {
            try {
                Log.d(TAG, "Connecting WebSocket")
                wsManager.connect(
                    onEvent = { event ->
                        when (event) {
                            is WsEvent.LocationUpdate -> {
                                Log.d(TAG, "WS: Friend location update: ${event.data.userId}")
                            }
                            else -> { /* events handled by ViewModels when UI is visible */ }
                        }
                    },
                    onDisconnect = { reason ->
                        Log.w(TAG, "WS disconnected: $reason. Reconnecting in 10s...")
                        // onDisconnect is not a suspend lambda — launch a new coroutine for delay
                        serviceScope.launch {
                            delay(10_000L)
                            connectWebSocket()
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "WS connect error: ${e.message}")
                serviceScope.launch {
                    delay(10_000L)
                    connectWebSocket()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "Service destroying")
        trackingJob?.cancel()
        wsJob?.cancel()
        serviceScope.cancel()
        wsManager.disconnect()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        super.onDestroy()
    }
}

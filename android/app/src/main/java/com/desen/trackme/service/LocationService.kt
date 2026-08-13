package com.desen.trackme.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.desen.trackme.MainActivity
import com.desen.trackme.R
import com.desen.trackme.core.network.dto.LocationUpdateRequest
import com.desen.trackme.data.PendingLocationStore
import com.desen.trackme.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * Persistent ForegroundService that records GPS fixes via the Fused Location
 * Provider and POSTs them to the backend on a fixed interval. It renders
 * nothing — this is the "headless" telemetry engine from plan.txt.
 */
@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var pendingStore: PendingLocationStore

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationCallback: LocationCallback? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTracking()
        } else {
            startTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        createChannel()
        startForegroundCompat()
        isRunning = true

        if (locationCallback != null) return

        // Fused Location Provider at highest accuracy, sampled every 5 seconds.
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_INTERVAL_MS
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                handleLocation(location)
            }
        }
        locationCallback = callback

        if (hasLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }
    }

    private fun handleLocation(location: Location) {
        val request = LocationUpdateRequest(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy.takeIf { location.hasAccuracy() }?.toDouble(),
            altitude = location.altitude.takeIf { location.hasAltitude() },
            bearing = location.bearing.takeIf { location.hasBearing() }?.toDouble(),
            batteryPercentage = batteryLevel(),
            isCharging = isCharging(),
            isMock = location.isFromMockProvider,
            timestamp = Instant.ofEpochMilli(location.time).toString()
        )

        serviceScope.launch {
            val ok = locationRepository.postLocation(request)
            if (!ok) {
                pendingStore.enqueue(request)
                SyncWorker.enqueue(this@LocationService)
            }
            notifySynced()
        }
    }

    private fun stopTracking() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    private fun startForegroundCompat() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Background GPS tracking status" }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stop = PendingIntent.getService(
            this, 1,
            Intent(this, LocationService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("TrackMe")
            .setContentText("Recording location in background")
            .setOngoing(true)
            .setContentIntent(openApp)
            .addAction(0, "Stop", stop)
            .build()
    }

    private fun batteryLevel(): Int {
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun isCharging(): Boolean {
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        val status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun notifySynced() {
        sendBroadcast(
            Intent(ACTION_LOCATION_SYNCED).setPackage(packageName)
        )
    }

    companion object {
        const val ACTION_START = "com.desen.trackme.action.START"
        const val ACTION_STOP = "com.desen.trackme.action.STOP"
        const val ACTION_LOCATION_SYNCED = "com.desen.trackme.action.LOCATION_SYNCED"
        const val CHANNEL_ID = "location_tracking"
        const val NOTIFICATION_ID = 1001

        // Fixed sampling interval: record every 5 seconds.
        const val LOCATION_INTERVAL_MS = 5_000L

        @Volatile
        var isRunning: Boolean = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, LocationService::class.java).setAction(ACTION_START)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationService::class.java).setAction(ACTION_STOP)
            context.startService(intent)
        }
    }
}

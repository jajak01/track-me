package com.trackme.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackMeApp : Application() {

    companion object {
        const val LOCATION_CHANNEL_ID = "track_me_location"
        const val LOCATION_CHANNEL_NAME = "Location Tracking"
        const val LOCATION_NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channel = NotificationChannel(
            LOCATION_CHANNEL_ID,
            LOCATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shown while Track Me is tracking your location in the background"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

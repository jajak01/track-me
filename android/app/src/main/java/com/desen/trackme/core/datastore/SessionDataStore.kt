package com.desen.trackme.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.desen.trackme.core.session.Session
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session")

/**
 * Persistent, encrypted-at-rest-when-device-is-encrypted storage for the JWT pair.
 * This is what gives the app its "login forever" behavior: the refresh token survives
 * app restarts and device reboots, and the app silently re-authenticates on startup.
 */
class SessionDataStore(private val context: Context) {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val EMAIL = stringPreferencesKey("email")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
    }

    suspend fun saveSession(session: Session) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = session.accessToken
            prefs[Keys.REFRESH_TOKEN] = session.refreshToken
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.EMAIL] = session.email
            prefs[Keys.DISPLAY_NAME] = session.displayName
        }
    }

    suspend fun loadSession(): Session? {
        val prefs = context.dataStore.data.first()
        val accessToken = prefs[Keys.ACCESS_TOKEN] ?: return null
        val refreshToken = prefs[Keys.REFRESH_TOKEN] ?: return null
        return Session(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = prefs[Keys.USER_ID] ?: "",
            email = prefs[Keys.EMAIL] ?: "",
            displayName = prefs[Keys.DISPLAY_NAME] ?: ""
        )
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

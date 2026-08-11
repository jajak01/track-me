package com.trackme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("trackme_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val DISPLAY_NAME = stringPreferencesKey("display_name")
    }

    // In-memory cache to avoid runBlocking in OkHttp interceptors
    @Volatile private var cachedAccessToken: String? = null
    @Volatile private var cachedRefreshToken: String? = null
    @Volatile private var cachedUserId: String? = null

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val displayName: Flow<String?> = context.dataStore.data.map { it[DISPLAY_NAME] }
    val isLoggedIn: Flow<Boolean> = accessToken.map { it != null }

    suspend fun saveTokens(access: String, refresh: String) {
        cachedAccessToken = access
        cachedRefreshToken = refresh
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUser(userId: String, email: String, name: String) {
        cachedUserId = userId
        context.dataStore.edit {
            it[USER_ID] = userId
            it[USER_EMAIL] = email
            it[DISPLAY_NAME] = name
        }
    }

    suspend fun clear() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null
        context.dataStore.edit { it.clear() }
    }

    /** Non-blocking: use from OkHttp interceptors (called on OkHttp threads) */
    fun getAccessTokenSync(): String? = cachedAccessToken
    fun getRefreshTokenSync(): String? = cachedRefreshToken

    /** Update in-memory cache and persist to disk in background */
    fun saveTokensSync(access: String, refresh: String) {
        cachedAccessToken = access
        cachedRefreshToken = refresh
        GlobalScope.launch(Dispatchers.IO) {
            try {
                context.dataStore.edit {
                    it[ACCESS_TOKEN] = access
                    it[REFRESH_TOKEN] = refresh
                }
            } catch (_: Exception) {}
        }
    }

    fun clearSync() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null
        GlobalScope.launch(Dispatchers.IO) {
            try {
                context.dataStore.edit { it.clear() }
            } catch (_: Exception) {}
        }
    }

    suspend fun getAccessToken(): String? {
        val cached = cachedAccessToken
        if (cached != null) return cached
        cachedAccessToken = context.dataStore.data.first()[ACCESS_TOKEN]
        return cachedAccessToken
    }

    suspend fun getRefreshToken(): String? {
        val cached = cachedRefreshToken
        if (cached != null) return cached
        cachedRefreshToken = context.dataStore.data.first()[REFRESH_TOKEN]
        return cachedRefreshToken
    }

    suspend fun getUserId(): String? {
        val cached = cachedUserId
        if (cached != null) return cached
        cachedUserId = context.dataStore.data.first()[USER_ID]
        return cachedUserId
    }
}

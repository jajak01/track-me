package com.trackme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val displayName: Flow<String?> = context.dataStore.data.map { it[DISPLAY_NAME] }
    val isLoggedIn: Flow<Boolean> = accessToken.map { it != null }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUser(userId: String, email: String, name: String) {
        context.dataStore.edit {
            it[USER_ID] = userId
            it[USER_EMAIL] = email
            it[DISPLAY_NAME] = name
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getAccessToken(): String? = context.dataStore.data.first()[ACCESS_TOKEN]
    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[REFRESH_TOKEN]
    suspend fun getUserId(): String? = context.dataStore.data.first()[USER_ID]
}

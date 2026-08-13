package com.desen.trackme.data.repository

import com.desen.trackme.core.network.ApiService
import com.desen.trackme.core.network.dto.LoginRequest
import com.desen.trackme.core.network.dto.LoginResponse
import com.desen.trackme.core.network.dto.RefreshRequest
import com.desen.trackme.core.network.dto.RegisterRequest
import com.desen.trackme.core.session.Session
import com.desen.trackme.core.session.TokenStore
import com.desen.trackme.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

class ApiException(message: String) : Exception(message)

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): Result<Session> {
        return try {
            val reg = api.register(RegisterRequest(email, password, displayName))
            if (!reg.success || reg.data == null) {
                throw ApiException(reg.message.ifBlank { "Registration failed" })
            }

            // One-time register flow: auto-login right after registration.
            val login = api.login(LoginRequest(email, password))
            if (!login.success || login.data == null) {
                throw ApiException(login.message.ifBlank { "Login failed" })
            }

            val session = login.data!!.toSession()
            tokenStore.update(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Session> {
        return try {
            val resp = api.login(LoginRequest(email, password))
            if (!resp.success || resp.data == null) {
                throw ApiException(resp.message.ifBlank { "Invalid email or password" })
            }
            val session = resp.data!!.toSession()
            tokenStore.update(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        val refreshToken = tokenStore.refreshToken()
        if (refreshToken != null) {
            try {
                api.logout(RefreshRequest(refreshToken))
            } catch (e: Exception) {
                // Best-effort revocation — ignore network/auth errors.
            }
        }
        tokenStore.clear()
    }

    override suspend fun isLoggedIn(): Boolean {
        tokenStore.load()
        return tokenStore.current() != null
    }

    override suspend fun currentSession(): Session? {
        tokenStore.load()
        return tokenStore.current()
    }
}

private fun LoginResponse.toSession() = Session(
    accessToken = accessToken,
    refreshToken = refreshToken,
    userId = user.id,
    email = user.email,
    displayName = user.displayName
)

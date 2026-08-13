package com.desen.trackme.core.network

import com.desen.trackme.core.network.dto.RefreshRequest
import com.desen.trackme.core.session.Session
import com.desen.trackme.core.session.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * On a 401 response, silently calls `POST /auth/refresh` with the stored refresh
 * token, persists the new pair, and retries the original request once. This is
 * what keeps the background service authenticated forever without user action.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApi: RefreshApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Give up after one retry to avoid an infinite refresh loop.
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenStore.refreshToken() ?: return null

        val refreshResponse = runBlocking {
            try {
                refreshApi.refresh(RefreshRequest(refreshToken))
            } catch (e: Exception) {
                null
            }
        } ?: return null

        val data = refreshResponse.data ?: return null
        if (data.accessToken.isBlank() || data.refreshToken.isBlank()) return null

        runBlocking {
            tokenStore.update(
                Session(
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken,
                    userId = data.user.id,
                    email = data.user.email,
                    displayName = data.user.displayName
                )
            )
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${data.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

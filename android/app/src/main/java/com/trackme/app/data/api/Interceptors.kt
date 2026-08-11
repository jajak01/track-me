package com.trackme.app.data.api

import com.trackme.app.data.local.TokenManager
import com.trackme.app.data.model.RefreshRequest
import com.trackme.app.data.model.LoginResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody // <-- Tambahkan import ini
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getAccessToken() }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    private val json = Json { ignoreUnknownKeys = true }
    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return null
        if (refreshToken.isBlank()) return null

        val refreshRequest = Request.Builder()
            .url("${response.request.url.scheme}://${response.request.url.host}:${response.request.url.port}/api/v1/auth/refresh")
            .post(
                json.encodeToString(RefreshRequest.serializer(), RefreshRequest(refreshToken))
                    .toRequestBody("application/json".toMediaType())
            )
            .build()

        val refreshResponse = refreshClient.newCall(refreshRequest).execute()

        return if (refreshResponse.isSuccessful) {
            val body = refreshResponse.body?.string()
            val loginResp = body?.let { json.decodeFromString<com.trackme.app.data.model.ApiResponse<LoginResponse>>(it) }
            loginResp?.data?.let { newTokens ->
                runBlocking {
                    tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                }
                response.request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            }
        } else {
            runBlocking { tokenManager.clear() }
            null
        }
    }
}
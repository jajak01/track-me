package com.desen.trackme.core.network

import com.desen.trackme.core.network.dto.ApiResponse
import com.desen.trackme.core.network.dto.LoginResponse
import com.desen.trackme.core.network.dto.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Minimal API surface used only by the OkHttp [Authenticator] to rotate tokens.
 * It must NOT carry the authenticator itself, otherwise a 401 during refresh
 * would recurse forever.
 */
interface RefreshApiService {
    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): ApiResponse<LoginResponse>
}

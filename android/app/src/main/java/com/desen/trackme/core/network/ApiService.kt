package com.desen.trackme.core.network

import com.desen.trackme.core.network.dto.ApiResponse
import com.desen.trackme.core.network.dto.LocationResponse
import com.desen.trackme.core.network.dto.LocationUpdateRequest
import com.desen.trackme.core.network.dto.LoginRequest
import com.desen.trackme.core.network.dto.LoginResponse
import com.desen.trackme.core.network.dto.RefreshRequest
import com.desen.trackme.core.network.dto.RegisterRequest
import com.desen.trackme.core.network.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): ApiResponse<LoginResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: RefreshRequest): ApiResponse<Unit>

    @POST("api/v1/location/update")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): ApiResponse<LocationResponse>
}

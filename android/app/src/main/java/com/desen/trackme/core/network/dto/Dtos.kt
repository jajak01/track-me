package com.desen.trackme.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wraps the backend `dto.APIResponse{success,message,data,errors}` envelope. */
@Serializable
data class ApiResponse<T>(
    @SerialName("success") val success: Boolean = false,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: T? = null,
    @SerialName("errors") val errors: List<String> = emptyList()
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("display_name") val displayName: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    @SerialName("display_name") val displayName: String = "",
    val avatar: String = "",
    val phone: String = "",
    @SerialName("status_message") val statusMessage: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    val user: UserResponse
)

/** Mirrors the plan.txt `POST /api/v1/location/update` payload (minus speed/activity). */
@Serializable
data class LocationUpdateRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null,
    val altitude: Double? = null,
    val bearing: Double? = null,
    @SerialName("battery_percentage") val batteryPercentage: Int? = null,
    @SerialName("is_charging") val isCharging: Boolean = false,
    @SerialName("is_mock") val isMock: Boolean = false,
    val timestamp: String? = null
)

@Serializable
data class LocationResponse(
    @SerialName("user_id") val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Double? = null,
    val altitude: Double? = null,
    val bearing: Double? = null,
    val speed: Double? = null,
    @SerialName("battery_percentage") val batteryPercentage: Int? = null,
    @SerialName("is_charging") val isCharging: Boolean = false,
    @SerialName("activity_type") val activityType: String? = null,
    @SerialName("is_mock") val isMock: Boolean = false,
    val timestamp: String? = null
)

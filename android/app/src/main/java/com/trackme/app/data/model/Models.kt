@file:OptIn(ExperimentalSerializationApi::class)

package com.trackme.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi

// --- Generic API wrapper ---
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null
)

// --- Auth ---
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
data class ChangePasswordRequest(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    @SerialName("display_name") val displayName: String,
    val avatar: String = "",
    val phone: String = "",
    @SerialName("status_message") val statusMessage: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("display_name") val displayName: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    @SerialName("status_message") val statusMessage: String? = null
)

// --- Friends ---
@Serializable
data class SendFriendRequest(
    @SerialName("receiver_email") val receiverEmail: String
)

@Serializable
data class RespondFriendRequest(
    @SerialName("request_id") val requestId: String,
    val accept: Boolean
)

@Serializable
data class FriendRequestResponse(
    val id: String,
    val sender: UserResponse,
    val receiver: UserResponse,
    val status: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class BlockUserRequest(
    @SerialName("user_id") val userId: String
)

// --- Sharing ---
@Serializable
data class SharingRequest(
    @SerialName("friend_id") val friendId: String
)

@Serializable
data class SharingApproval(
    @SerialName("notification_id") val notificationId: String,
    val approve: Boolean
)

// --- Location ---
@Serializable
data class LocationUpdateRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double = 0.0,
    val altitude: Double = 0.0,
    val bearing: Double = 0.0,
    val speed: Double = 0.0,
    val battery: Int = 0,
    val charging: Boolean = false,
    val activity: String = "unknown",
    @SerialName("mock_location") val mockLocation: Boolean = false,
    @SerialName("gps_provider") val gpsProvider: String = "gps"
)

@Serializable
data class LocationResponse(
    @SerialName("user_id") val userId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double = 0.0,
    val altitude: Double = 0.0,
    val bearing: Double = 0.0,
    val speed: Double = 0.0,
    val battery: Int = 0,
    val charging: Boolean = false,
    val activity: String = "unknown",
    @SerialName("mock_location") val mockLocation: Boolean = false,
    @SerialName("gps_provider") val gpsProvider: String = "",
    val timestamp: String = ""
)

// --- Notifications ---
@Serializable
data class NotificationResponse(
    val id: String,
    @SerialName("user_id") val userId: String,
    val type: String,
    val title: String,
    val body: String,
    val data: String? = null,
    val read: Boolean = false,
    @SerialName("created_at") val createdAt: String = ""
)

// --- Pagination ---
@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String,
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val total: Long,
    @SerialName("total_pages") val totalPages: Int
)

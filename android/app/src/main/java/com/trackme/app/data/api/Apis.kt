package com.trackme.app.data.api

import com.trackme.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshRequest): Response<ApiResponse<Unit>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    @DELETE("auth/delete")
    suspend fun deleteAccount(): Response<ApiResponse<Unit>>
}

interface UserApi {
    @GET("user/profile")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>

    @PATCH("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>
}

interface FriendApi {
    @POST("friend/request")
    suspend fun sendRequest(@Body request: SendFriendRequest): Response<ApiResponse<FriendRequestResponse>>

    @POST("friend/respond")
    suspend fun respondRequest(@Body request: RespondFriendRequest): Response<ApiResponse<Unit>>

    @GET("friend/requests")
    suspend fun getPendingRequests(): Response<ApiResponse<List<FriendRequestResponse>>>

    @GET("friend/list")
    suspend fun getFriends(): Response<ApiResponse<List<UserResponse>>>

    @DELETE("friend/remove/{id}")
    suspend fun removeFriend(@Path("id") friendId: String): Response<ApiResponse<Unit>>

    @POST("friend/block")
    suspend fun blockUser(@Body request: BlockUserRequest): Response<ApiResponse<Unit>>

    @DELETE("friend/unblock/{id}")
    suspend fun unblockUser(@Path("id") userId: String): Response<ApiResponse<Unit>>

    @GET("friend/blocked")
    suspend fun getBlockedUsers(): Response<ApiResponse<List<UserResponse>>>
}

interface LocationApi {
    @POST("sharing/request")
    suspend fun requestSharing(@Body request: SharingRequest): Response<ApiResponse<Unit>>

    @POST("sharing/approve")
    suspend fun approveSharing(@Body request: SharingApproval): Response<ApiResponse<Unit>>

    @DELETE("sharing/revoke/{id}")
    suspend fun revokeSharing(@Path("id") friendId: String): Response<ApiResponse<Unit>>

    @POST("location/update")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): Response<ApiResponse<LocationResponse>>

    @GET("location/current/{userId}")
    suspend fun getCurrentLocation(@Path("userId") userId: String): Response<ApiResponse<LocationResponse>>

    @GET("location/history/{userId}")
    suspend fun getLocationHistory(
        @Path("userId") userId: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<ApiResponse<List<LocationResponse>>>
}

interface NotificationApi {
    @GET("notification/list")
    suspend fun getNotifications(): Response<PaginatedResponse<NotificationResponse>>

    @PATCH("notification/read/{id}")
    suspend fun markAsRead(@Path("id") notificationId: String): Response<ApiResponse<Unit>>

    @POST("notification/read-all")
    suspend fun markAllAsRead(): Response<ApiResponse<Unit>>
}

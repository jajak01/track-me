package com.trackme.app.data.repository

import com.trackme.app.data.api.AuthApi
import com.trackme.app.data.local.TokenManager
import com.trackme.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun register(email: String, password: String, displayName: String): Result<UserResponse> {
        val response = api.register(RegisterRequest(email, password, displayName))
        return if (response.isSuccessful && response.body()?.success == true) {
            Result.Success(response.body()!!.data!!)
        } else {
            Result.Error(response.body()?.message ?: "Registration failed")
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        val response = api.login(LoginRequest(email, password))
        return if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data!!
            tokenManager.saveTokens(data.accessToken, data.refreshToken)
            tokenManager.saveUser(data.user.id, data.user.email, data.user.displayName)
            Result.Success(data)
        } else {
            Result.Error(response.body()?.message ?: "Login failed")
        }
    }

    suspend fun refreshToken(): Result<LoginResponse> {
        val refresh = tokenManager.getRefreshToken() ?: return Result.Error("No refresh token")
        val response = api.refreshToken(RefreshRequest(refresh))
        return if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data!!
            tokenManager.saveTokens(data.accessToken, data.refreshToken)
            Result.Success(data)
        } else {
            tokenManager.clear()
            Result.Error("Session expired")
        }
    }

    suspend fun logout() {
        val refresh = tokenManager.getRefreshToken() ?: return
        api.logout(RefreshRequest(refresh))
        tokenManager.clear()
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> {
        val response = api.changePassword(ChangePasswordRequest(oldPass, newPass))
        return if (response.isSuccessful && response.body()?.success == true) {
            Result.Success(Unit)
        } else {
            Result.Error(response.body()?.message ?: "Failed to change password")
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        val response = api.deleteAccount()
        return if (response.isSuccessful && response.body()?.success == true) {
            tokenManager.clear()
            Result.Success(Unit)
        } else {
            Result.Error(response.body()?.message ?: "Failed to delete account")
        }
    }
}

@Singleton
class UserRepository @Inject constructor(
    private val api: com.trackme.app.data.api.UserApi
) {
    suspend fun getProfile(): Result<UserResponse> {
        val r = api.getProfile()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data!!)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserResponse> {
        val r = api.updateProfile(request)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data!!)
        else Result.Error(r.body()?.message ?: "Failed")
    }
}

@Singleton
class FriendRepository @Inject constructor(
    private val api: com.trackme.app.data.api.FriendApi
) {
    suspend fun sendRequest(email: String): Result<FriendRequestResponse> {
        val r = api.sendRequest(SendFriendRequest(email))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data!!)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun respondRequest(requestId: String, accept: Boolean): Result<Unit> {
        val r = api.respondRequest(RespondFriendRequest(requestId, accept))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getPendingRequests(): Result<List<FriendRequestResponse>> {
        val r = api.getPendingRequests()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data ?: emptyList())
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getFriends(): Result<List<UserResponse>> {
        val r = api.getFriends()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data ?: emptyList())
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun removeFriend(id: String): Result<Unit> {
        val r = api.removeFriend(id)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun blockUser(userId: String): Result<Unit> {
        val r = api.blockUser(BlockUserRequest(userId))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun unblockUser(userId: String): Result<Unit> {
        val r = api.unblockUser(userId)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getBlockedUsers(): Result<List<UserResponse>> {
        val r = api.getBlockedUsers()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data ?: emptyList())
        else Result.Error(r.body()?.message ?: "Failed")
    }
}

@Singleton
class LocationRepository @Inject constructor(
    private val api: com.trackme.app.data.api.LocationApi
) {
    suspend fun requestSharing(friendId: String): Result<Unit> {
        val r = api.requestSharing(SharingRequest(friendId))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun approveSharing(notificationId: String, approve: Boolean): Result<Unit> {
        val r = api.approveSharing(SharingApproval(notificationId, approve))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun revokeSharing(friendId: String): Result<Unit> {
        val r = api.revokeSharing(friendId)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun updateLocation(request: LocationUpdateRequest): Result<LocationResponse> {
        val r = api.updateLocation(request)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data!!)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getCurrentLocation(userId: String): Result<LocationResponse> {
        val r = api.getCurrentLocation(userId)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data!!)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getLocationHistory(userId: String, start: String, end: String): Result<List<LocationResponse>> {
        val r = api.getLocationHistory(userId, start, end)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data ?: emptyList())
        else Result.Error(r.body()?.message ?: "Failed")
    }
}

@Singleton
class NotificationRepository @Inject constructor(
    private val api: com.trackme.app.data.api.NotificationApi
) {
    suspend fun getNotifications(): Result<List<NotificationResponse>> {
        val r = api.getNotifications()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(r.body()!!.data)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun markAsRead(id: String): Result<Unit> {
        val r = api.markAsRead(id)
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun markAllAsRead(): Result<Unit> {
        val r = api.markAllAsRead()
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }
}

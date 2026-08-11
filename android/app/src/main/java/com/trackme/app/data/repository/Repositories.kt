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
        return try {
            val response = api.register(RegisterRequest(email, password, displayName))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.Success(body.data!!)
            } else {
                Result.Error(body?.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val data = body.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                tokenManager.saveUser(data.user.id, data.user.email, data.user.displayName)
                Result.Success(data)
            } else {
                Result.Error(body?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun refreshToken(): Result<LoginResponse> {
        val refresh = tokenManager.getRefreshToken() ?: return Result.Error("No refresh token")
        return try {
            val response = api.refreshToken(RefreshRequest(refresh))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val data = body.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                Result.Success(data)
            } else {
                tokenManager.clear()
                Result.Error("Session expired")
            }
        } catch (e: Exception) {
            tokenManager.clear()
            Result.Error("Network error: ${e.message}")
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
        return try {
            val r = api.getProfile()
            val body = r.body()
            if (r.isSuccessful && body?.success == true && body.data != null) Result.Success(body.data!!)
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserResponse> {
        return try {
            val r = api.updateProfile(request)
            val body = r.body()
            if (r.isSuccessful && body?.success == true && body.data != null) Result.Success(body.data!!)
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}

@Singleton
class FriendRepository @Inject constructor(
    private val api: com.trackme.app.data.api.FriendApi
) {
    suspend fun sendRequest(email: String): Result<FriendRequestResponse> {
        return try {
            val r = api.sendRequest(SendFriendRequest(email))
            val body = r.body()
            if (r.isSuccessful && body?.success == true && body.data != null) Result.Success(body.data!!)
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun respondRequest(requestId: String, accept: Boolean): Result<Unit> {
        val r = api.respondRequest(RespondFriendRequest(requestId, accept))
        return if (r.isSuccessful && r.body()?.success == true) Result.Success(Unit)
        else Result.Error(r.body()?.message ?: "Failed")
    }

    suspend fun getPendingRequests(): Result<List<FriendRequestResponse>> {
        return try {
            val r = api.getPendingRequests()
            val body = r.body()
            if (r.isSuccessful && body?.success == true) Result.Success(body.data ?: emptyList())
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun getFriends(): Result<List<UserResponse>> {
        return try {
            val r = api.getFriends()
            val body = r.body()
            if (r.isSuccessful && body?.success == true) Result.Success(body.data ?: emptyList())
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
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
        return try {
            val r = api.getBlockedUsers()
            val body = r.body()
            if (r.isSuccessful && body?.success == true) Result.Success(body.data ?: emptyList())
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
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
        return try {
            val r = api.updateLocation(request)
            val body = r.body()
            if (r.isSuccessful && body?.success == true && body.data != null) Result.Success(body.data!!)
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun getCurrentLocation(userId: String): Result<LocationResponse> {
        return try {
            val r = api.getCurrentLocation(userId)
            val body = r.body()
            if (r.isSuccessful && body?.success == true && body.data != null) Result.Success(body.data!!)
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun getLocationHistory(userId: String, start: String, end: String): Result<List<LocationResponse>> {
        return try {
            val r = api.getLocationHistory(userId, start, end)
            val body = r.body()
            if (r.isSuccessful && body?.success == true) Result.Success(body.data ?: emptyList())
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}

@Singleton
class NotificationRepository @Inject constructor(
    private val api: com.trackme.app.data.api.NotificationApi
) {
    suspend fun getNotifications(): Result<List<NotificationResponse>> {
        return try {
            val r = api.getNotifications()
            val body = r.body()
            if (r.isSuccessful && body?.success == true) Result.Success(body.data ?: emptyList())
            else Result.Error(body?.message ?: "Failed")
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
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

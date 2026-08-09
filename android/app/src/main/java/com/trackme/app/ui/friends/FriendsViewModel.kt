package com.trackme.app.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackme.app.data.model.FriendRequestResponse
import com.trackme.app.data.model.UserResponse
import com.trackme.app.data.repository.FriendRepository
import com.trackme.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendsState(
    val friends: List<UserResponse> = emptyList(),
    val pendingRequests: List<FriendRequestResponse> = emptyList(),
    val blockedUsers: List<UserResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsState())
    val state: StateFlow<FriendsState> = _state

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loadFriends()
            loadPending()
            loadBlocked()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadFriends() {
        when (val r = friendRepository.getFriends()) {
            is Result.Success -> _state.update { it.copy(friends = r.data) }
            is Result.Error -> _state.update { it.copy(error = r.message) }
        }
    }

    private suspend fun loadPending() {
        when (val r = friendRepository.getPendingRequests()) {
            is Result.Success -> _state.update { it.copy(pendingRequests = r.data) }
            is Result.Error -> {}
        }
    }

    private suspend fun loadBlocked() {
        when (val r = friendRepository.getBlockedUsers()) {
            is Result.Success -> _state.update { it.copy(blockedUsers = r.data) }
            is Result.Error -> {}
        }
    }

    fun sendRequest(email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val r = friendRepository.sendRequest(email)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, message = "Friend request sent!") }
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
            }
        }
    }

    fun respondRequest(requestId: String, accept: Boolean) {
        viewModelScope.launch {
            when (val r = friendRepository.respondRequest(requestId, accept)) {
                is Result.Success -> loadAll()
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            when (val r = friendRepository.removeFriend(friendId)) {
                is Result.Success -> loadAll()
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            when (val r = friendRepository.blockUser(userId)) {
                is Result.Success -> loadAll()
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            when (val r = friendRepository.unblockUser(userId)) {
                is Result.Success -> loadAll()
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun clearMessage() { _state.update { it.copy(message = null) } }
    fun clearError() { _state.update { it.copy(error = null) } }
}

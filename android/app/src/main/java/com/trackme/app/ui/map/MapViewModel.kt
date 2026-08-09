package com.trackme.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackme.app.data.api.WebSocketManager
import com.trackme.app.data.api.WsEvent
import com.trackme.app.data.model.LocationResponse
import com.trackme.app.data.model.LocationUpdateRequest
import com.trackme.app.data.model.UserResponse
import com.trackme.app.data.repository.FriendRepository
import com.trackme.app.data.repository.LocationRepository
import com.trackme.app.data.repository.Result
import com.trackme.app.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapState(
    val friends: List<UserResponse> = emptyList(),
    val friendLocations: Map<String, LocationResponse> = emptyMap(),
    val myLocation: LocationResponse? = null,
    val selectedFriend: UserResponse? = null,
    val selectedFriendLocation: LocationResponse? = null,
    val isSharing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val locationRepository: LocationRepository,
    private val wsManager: WebSocketManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state

    init {
        connectWs()
        loadFriends()
    }

    private fun connectWs() {
        wsManager.connect(
            onEvent = { event ->
                when (event) {
                    is WsEvent.LocationUpdate -> {
                        _state.update {
                            it.copy(friendLocations = it.friendLocations + (event.data.userId to event.data))
                        }
                    }
                    is WsEvent.Notification -> { /* handled by notifications VM */ }
                    is WsEvent.SharingUpdate -> loadFriends()
                    else -> {}
                }
            },
            onDisconnect = { /* auto-reconnect could be added */ }
        )
    }

    fun loadFriends() {
        viewModelScope.launch {
            when (val result = friendRepository.getFriends()) {
                is Result.Success -> _state.update { it.copy(friends = result.data) }
                is Result.Error -> _state.update { it.copy(error = result.message) }
            }
        }
    }

    fun updateMyLocation(lat: Double, lng: Double, battery: Int = 0, activity: String = "unknown") {
        viewModelScope.launch {
            val req = LocationUpdateRequest(latitude = lat, longitude = lng, battery = battery, activity = activity)
            when (val result = locationRepository.updateLocation(req)) {
                is Result.Success -> _state.update { it.copy(myLocation = result.data) }
                is Result.Error -> {}
            }
        }
    }

    fun selectFriend(friend: UserResponse) {
        viewModelScope.launch {
            _state.update { it.copy(selectedFriend = friend, selectedFriendLocation = null) }
            when (val result = locationRepository.getCurrentLocation(friend.id)) {
                is Result.Success -> _state.update { it.copy(selectedFriendLocation = result.data) }
                is Result.Error -> {}
            }
        }
    }

    fun requestSharing(friendId: String) {
        viewModelScope.launch {
            when (val result = locationRepository.requestSharing(friendId)) {
                is Result.Success -> {}
                is Result.Error -> _state.update { it.copy(error = result.message) }
            }
        }
    }

    fun revokeSharing(friendId: String) {
        viewModelScope.launch {
            locationRepository.revokeSharing(friendId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.disconnect()
    }
}

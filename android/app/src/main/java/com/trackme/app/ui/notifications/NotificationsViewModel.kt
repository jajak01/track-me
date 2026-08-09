package com.trackme.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackme.app.data.model.NotificationResponse
import com.trackme.app.data.repository.LocationRepository
import com.trackme.app.data.repository.NotificationRepository
import com.trackme.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<NotificationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = notificationRepository.getNotifications()) {
                is Result.Success -> _state.update { it.copy(isLoading = false, notifications = r.data) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
            load()
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
            load()
        }
    }

    fun approveSharing(notificationId: String, approve: Boolean) {
        viewModelScope.launch {
            when (val r = locationRepository.approveSharing(notificationId, approve)) {
                is Result.Success -> {
                    markAsRead(notificationId)
                    _state.update { it.copy(message = if (approve) "Sharing approved!" else "Sharing denied") }
                }
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun clearMessage() { _state.update { it.copy(message = null, error = null) } }
}

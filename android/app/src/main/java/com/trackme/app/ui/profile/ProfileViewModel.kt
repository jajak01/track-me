package com.trackme.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackme.app.data.model.UserResponse
import com.trackme.app.data.model.UpdateProfileRequest
import com.trackme.app.data.repository.AuthRepository
import com.trackme.app.data.repository.Result
import com.trackme.app.data.repository.UserRepository
import com.trackme.app.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: UserResponse? = null,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = userRepository.getProfile()) {
                is Result.Success -> _state.update { it.copy(isLoading = false, user = r.data) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
            }
        }
    }

    fun updateProfile(name: String? = null, status: String? = null, phone: String? = null) {
        viewModelScope.launch {
            when (val r = userRepository.updateProfile(
                UpdateProfileRequest(displayName = name, statusMessage = status, phone = phone)
            )) {
                is Result.Success -> {
                    _state.update { it.copy(user = r.data, message = "Profile updated!") }
                }
                is Result.Error -> _state.update { it.copy(error = r.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }

    fun clearMessage() { _state.update { it.copy(message = null, error = null) } }
}

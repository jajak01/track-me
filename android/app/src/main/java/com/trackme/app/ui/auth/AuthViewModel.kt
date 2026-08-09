package com.trackme.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackme.app.data.repository.AuthRepository
import com.trackme.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(email, password, displayName)) {
                is Result.Success -> {
                    // Auto-login after register
                    login(email, password)
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun clearError() { _state.update { it.copy(error = null) } }
    fun resetLoginState() { _state.update { it.copy(isLoggedIn = false) } }
}

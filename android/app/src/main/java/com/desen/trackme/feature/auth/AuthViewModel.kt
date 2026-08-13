package com.desen.trackme.feature.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desen.trackme.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun onDisplayNameChange(value: String) {
        uiState = uiState.copy(displayName = value, error = null)
    }

    fun login() {
        if (!validate()) return
        viewModelScope.launch {
            uiState = uiState.copy(loading = true)
            authRepository.login(uiState.email.trim(), uiState.password).fold(
                onSuccess = { uiState = uiState.copy(loading = false, loggedIn = true) },
                onFailure = { uiState = uiState.copy(loading = false, error = it.message ?: "Login failed") }
            )
        }
    }

    fun register() {
        if (!validate(requireName = true)) return
        viewModelScope.launch {
            uiState = uiState.copy(loading = true)
            authRepository.register(
                email = uiState.email.trim(),
                password = uiState.password,
                displayName = uiState.displayName.trim()
            ).fold(
                onSuccess = { uiState = uiState.copy(loading = false, loggedIn = true) },
                onFailure = { uiState = uiState.copy(loading = false, error = it.message ?: "Registration failed") }
            )
        }
    }

    private fun validate(requireName: Boolean = false): Boolean {
        val email = uiState.email.trim()
        val password = uiState.password
        return when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                uiState = uiState.copy(error = "Enter a valid email"); false
            }
            password.length < 8 -> {
                uiState = uiState.copy(error = "Password must be at least 8 characters"); false
            }
            requireName && uiState.displayName.trim().length < 2 -> {
                uiState = uiState.copy(error = "Display name must be at least 2 characters"); false
            }
            else -> true
        }
    }
}

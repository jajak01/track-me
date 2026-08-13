package com.desen.trackme.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desen.trackme.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /** null = still checking the persisted session. */
    var loggedIn by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch { loggedIn = authRepository.isLoggedIn() }
    }

    fun onLoggedIn() {
        loggedIn = true
    }

    fun onLoggedOut() {
        loggedIn = false
    }
}

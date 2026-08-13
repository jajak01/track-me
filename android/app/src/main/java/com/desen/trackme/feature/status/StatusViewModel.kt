package com.desen.trackme.feature.status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desen.trackme.core.session.Session
import com.desen.trackme.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var session by mutableStateOf<Session?>(null)
        private set

    init {
        viewModelScope.launch { session = authRepository.currentSession() }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            session = null
        }
    }
}

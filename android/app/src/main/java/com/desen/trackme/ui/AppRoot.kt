package com.desen.trackme.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.desen.trackme.feature.auth.AuthScreen
import com.desen.trackme.feature.status.StatusScreen

/**
 * Top-level navigation. If a persisted session exists the app jumps straight to
 * the status screen (silent re-authentication = "login forever"); otherwise it
 * shows the one-time auth screen.
 */
@Composable
fun AppRoot(rootViewModel: RootViewModel = hiltViewModel()) {
    when (val loggedIn = rootViewModel.loggedIn) {
        null -> LoadingScreen()
        true -> StatusScreen(onLogout = rootViewModel::onLoggedOut)
        false -> AuthScreen(onLoggedIn = rootViewModel::onLoggedIn)
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

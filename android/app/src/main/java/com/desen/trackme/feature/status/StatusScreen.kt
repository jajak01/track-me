package com.desen.trackme.feature.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.desen.trackme.service.LocationService
import kotlinx.coroutines.delay

/**
 * Minimal status dashboard: shows who is signed in, a tracking toggle, and a
 * logout button. No map is rendered — telemetry happens entirely in the service.
 */
@Composable
fun StatusScreen(
    onLogout: () -> Unit,
    viewModel: StatusViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var tracking by remember { mutableStateOf(LocationService.isRunning) }
    val session = viewModel.session

    LaunchedEffect(Unit) {
        while (true) {
            tracking = LocationService.isRunning
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("TrackMe", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Signed in as ${session?.email ?: "…"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Background tracking", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = tracking,
                onCheckedChange = { checked ->
                    tracking = checked
                    if (checked) LocationService.start(context) else LocationService.stop(context)
                }
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (tracking) "Recording & posting coordinates in background"
            else "Tracking stopped",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(40.dp))
        Button(onClick = {
            LocationService.stop(context)
            viewModel.logout()
            onLogout()
        }) {
            Text("Logout")
        }
    }
}

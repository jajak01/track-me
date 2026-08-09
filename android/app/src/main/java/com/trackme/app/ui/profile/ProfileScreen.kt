package com.trackme.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(state.user) { mutableStateOf(state.user?.displayName ?: "") }
    var editStatus by remember(state.user) { mutableStateOf(state.user?.statusMessage ?: "") }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.user?.let { user ->
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(96.dp))
                Spacer(Modifier.height(16.dp))
                Text(user.displayName, style = MaterialTheme.typography.headlineMedium)
                Text(user.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (user.statusMessage.isNotBlank()) {
                    Text("\"${user.statusMessage}\"", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        editName = user.displayName
                        editStatus = user.statusMessage
                        showEditDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Edit Profile") }
                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = { viewModel.logout() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Logout")
                }
            }

            state.message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Display Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = editStatus, onValueChange = { editStatus = it }, label = { Text("Status Message") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProfile(name = editName.trim().ifBlank { null }, status = editStatus.trim().ifBlank { null })
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }
}

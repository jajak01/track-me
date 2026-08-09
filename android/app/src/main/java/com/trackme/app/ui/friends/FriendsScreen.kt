package com.trackme.app.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var addEmail by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.PersonAdd, "Add Friend")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Friends (${state.friends.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Requests (${state.pendingRequests.size})") })
            }

            when (selectedTab) {
                0 -> FriendsList(state.friends, onRemove = { viewModel.removeFriend(it) }, onBlock = { viewModel.blockUser(it) })
                1 -> RequestsList(state.pendingRequests, onAccept = { viewModel.respondRequest(it, true) }, onReject = { viewModel.respondRequest(it, false) })
            }

            state.error?.let {
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(it) },
                    confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
                )
            }

            state.message?.let {
                AlertDialog(
                    onDismissRequest = { viewModel.clearMessage() },
                    title = { Text("Success") },
                    text = { Text(it) },
                    confirmButton = { TextButton(onClick = { viewModel.clearMessage() }) { Text("OK") } }
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Friend") },
            text = {
                OutlinedTextField(
                    value = addEmail,
                    onValueChange = { addEmail = it },
                    label = { Text("Friend's email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.sendRequest(addEmail.trim())
                    showAddDialog = false
                    addEmail = ""
                }) { Text("Send") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun FriendsList(friends: List<com.trackme.app.data.model.UserResponse>, onRemove: (String) -> Unit, onBlock: (String) -> Unit) {
    if (friends.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No friends yet. Add one with the + button!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn {
            items(friends) { friend ->
                ListItem(
                    headlineContent = { Text(friend.displayName) },
                    supportingContent = { Text(friend.email) },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onRemove(friend.id) }) {
                                Icon(Icons.Default.PersonRemove, "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                            IconButton(onClick = { onBlock(friend.id) }) {
                                Icon(Icons.Default.Block, "Block", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun RequestsList(
    requests: List<com.trackme.app.data.model.FriendRequestResponse>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending friend requests", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn {
            items(requests) { req ->
                ListItem(
                    headlineContent = { Text(req.sender.displayName) },
                    supportingContent = { Text(req.sender.email) },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onAccept(req.id) }) {
                                Icon(Icons.Default.Check, "Accept", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onReject(req.id) }) {
                                Icon(Icons.Default.Close, "Reject", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

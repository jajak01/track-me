package com.trackme.app.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackme.app.data.model.NotificationResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    TextButton(onClick = { viewModel.markAllAsRead() }) { Text("Mark all read") }
                }
            )
        }
    ) { padding ->
        if (state.notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No notifications", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(state.notifications) { notif ->
                    NotificationItem(
                        notification = notif,
                        onMarkRead = { viewModel.markAsRead(notif.id) },
                        onApprove = { viewModel.approveSharing(notif.id, true) },
                        onDeny = { viewModel.approveSharing(notif.id, false) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationResponse,
    onMarkRead: () -> Unit,
    onApprove: () -> Unit,
    onDeny: () -> Unit
) {
    val bgColor = if (notification.read) Color.Transparent else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)

    ListItem(
        headlineContent = { Text(notification.title, style = MaterialTheme.typography.titleSmall) },
        supportingContent = { Text(notification.body) },
        leadingContent = {
            Icon(
                when (notification.type) {
                    "friend_request" -> Icons.Default.PersonAdd
                    "friend_accepted" -> Icons.Default.Check
                    "sharing_request" -> Icons.Default.LocationOn
                    "sharing_approved" -> Icons.Default.CheckCircle
                    "sharing_revoked" -> Icons.Default.Cancel
                    else -> Icons.Default.Notifications
                },
                contentDescription = null,
                tint = if (notification.read) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Row {
                if (notification.type == "sharing_request" && !notification.read) {
                    IconButton(onClick = onApprove) { Icon(Icons.Default.Check, "Approve", tint = Color(0xFF43A047)) }
                    IconButton(onClick = onDeny) { Icon(Icons.Default.Close, "Deny", tint = MaterialTheme.colorScheme.error) }
                } else if (!notification.read) {
                    IconButton(onClick = onMarkRead) { Icon(Icons.Default.Done, "Mark read") }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = bgColor)
    )
}

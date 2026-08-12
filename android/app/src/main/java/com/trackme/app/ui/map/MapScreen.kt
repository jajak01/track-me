package com.trackme.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.annotations.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var hasLocationPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Battery optimization bypass
    val batteryOptimizationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* User may have granted or denied — we just continue */ }

    // Check and request battery optimization bypass when tracking starts
    LaunchedEffect(state.isTracking) {
        if (state.isTracking) {
            val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                try {
                    val intent = android.content.Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    batteryOptimizationLauncher.launch(intent)
                } catch (_: Exception) {
                    // Some devices don't support this intent
                }
            }
        }
    }

    // Track markers — reset when map changes
    var myMarker by remember { mutableStateOf<Marker?>(null) }
    val friendMarkers = remember { mutableMapOf<String, Marker>() }

    // Initialize MapLibre
    MapLibre.getInstance(context)

    // Create MapView. Each composition entry creates a fresh MapView
    // (Compose Navigation destroys composables on tab switch, so this runs anew each time).
    val mapView = remember {
        MapView(context).also { mv ->
            mv.onCreate(null)
            mv.getMapAsync { map ->
                mapLibreMap = map
                map.setStyle("https://basemaps.cartocdn.com/gl/positron-gl-style/style.json") {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(-6.2088, 106.8456), 12.0)
                    )
                    try {
                        if (hasLocationPermission) {
                            map.locationComponent.isLocationComponentEnabled = true
                        }
                    } catch (_: SecurityException) {}
                }
            }
        }
    }

    // Forward lifecycle events to MapView
    DisposableEffect(lifecycleOwner) {
        val currentState = lifecycleOwner.lifecycle.currentState
        if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
            mapView.onStart()
        }
        if (currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView.onResume()
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Clean up markers and map reference when composable leaves
    DisposableEffect(Unit) {
        onDispose {
            myMarker = null
            friendMarkers.values.forEach { it.remove() }
            friendMarkers.clear()
            mapLibreMap = null
        }
    }

    // Reconnect WebSocket + refresh friends on every re-entry
    LaunchedEffect(Unit) {
        viewModel.ensureConnected()
    }

    // Update my location marker when myLocation changes
    LaunchedEffect(state.myLocation, mapLibreMap) {
        val loc = state.myLocation ?: return@LaunchedEffect
        val map = mapLibreMap ?: return@LaunchedEffect
        myMarker?.remove()
        myMarker = map.addMarker(
            MarkerOptions()
                .position(LatLng(loc.latitude, loc.longitude))
                .title("Me")
                .snippet("Battery: ${loc.battery}%")
        )
    }

    // Update friend location markers when friendLocations change
    LaunchedEffect(state.friendLocations, mapLibreMap) {
        val map = mapLibreMap ?: return@LaunchedEffect
        val currentIds = state.friendLocations.keys
        val toRemove = friendMarkers.keys.filter { it !in currentIds }
        toRemove.forEach { friendMarkers.remove(it)?.remove() }
        state.friendLocations.forEach { (userId, loc) ->
            val friend = state.friends.find { it.id == userId }
            friendMarkers[userId]?.remove()
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(loc.latitude, loc.longitude))
                    .title(friend?.displayName ?: "Friend")
                    .snippet("Battery: ${loc.battery}% | ${loc.activity}")
            )
            friendMarkers[userId] = marker
        }
    }

    // Animate to selected friend
    LaunchedEffect(state.selectedFriendLocation, mapLibreMap) {
        val loc = state.selectedFriendLocation ?: return@LaunchedEffect
        mapLibreMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 15.0)
        )
    }

    // Start GPS tracking as soon as permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.startLocationTracking()
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
        }
    }

    // Enable map location dot when map finishes loading
    LaunchedEffect(mapLibreMap) {
        mapLibreMap?.let { map ->
            if (hasLocationPermission) {
                try {
                    map.locationComponent.isLocationComponentEnabled = true
                } catch (_: SecurityException) {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Live Map") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )

            // Tracking status indicator
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).padding(8.dp),
                color = if (state.isTracking) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (state.isTracking) Icons.Default.MyLocation else Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (state.isTracking) "📍 Tracking active" 
                            else if (hasLocationPermission) "⚠ Enable GPS in Settings"
                            else "⚠ Location permission needed",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // My location info
            state.myLocation?.let { loc ->
                Card(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 44.dp, start = 8.dp, end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📍 My Location", style = MaterialTheme.typography.labelMedium)
                        Text("${"%.6f".format(loc.latitude)}, ${"%.6f".format(loc.longitude)}", style = MaterialTheme.typography.bodySmall)
                        Text("Accuracy: ${"%.1f".format(loc.accuracy)}m | Battery: ${loc.battery}%")
                    }
                }
            }

            // Selected friend info
            state.selectedFriendLocation?.let { loc ->
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${state.selectedFriend?.displayName ?: "Friend"}'s Location", style = MaterialTheme.typography.labelMedium)
                        Text("${"%.6f".format(loc.latitude)}, ${"%.6f".format(loc.longitude)} | Batt: ${loc.battery}%")
                        Text("Activity: ${loc.activity} | ${loc.timestamp.take(19).replace("T", " ")}")
                    }
                }
            }

            // Friend list FAB
            SmallFloatingActionButton(
                onClick = { /* opens friend selector */ },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.People, "Friends")
            }
        }
    }
}

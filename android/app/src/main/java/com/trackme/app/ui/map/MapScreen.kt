package com.trackme.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackme.app.data.model.LocationResponse
import com.trackme.app.data.model.UserResponse
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.annotations.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var hasLocationPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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

    MapLibre.getInstance(context)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Live Map") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Map View
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        onCreate(null)
                        getMapAsync { map ->
                            mapLibreMap = map
                            map.setStyle("https://basemaps.cartocdn.com/gl/positron-gl-style/style.json") {
                                // Style loaded — camera to a default position
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(-6.2088, 106.8456), 12.0)
                                )
                            }
                        }
                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // My location info
            state.myLocation?.let { loc ->
                Card(
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📍 My Location", style = MaterialTheme.typography.labelMedium)
                        Text("${loc.latitude}, ${loc.longitude}", style = MaterialTheme.typography.bodySmall)
                        Text("Battery: ${loc.battery}% | ${loc.activity}")
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
                        Text("${loc.latitude}, ${loc.longitude} | Batt: ${loc.battery}%")
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

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}

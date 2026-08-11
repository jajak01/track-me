package com.trackme.app.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trackme.app.ui.auth.LoginScreen
import com.trackme.app.ui.auth.RegisterScreen
import com.trackme.app.ui.friends.FriendsScreen
import com.trackme.app.ui.map.MapScreen
import com.trackme.app.ui.notifications.NotificationsScreen
import com.trackme.app.ui.profile.ProfileScreen
import kotlinx.coroutines.flow.first

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object Friends : Screen("friends", "Friends", Icons.Default.People)
    data object Notifications : Screen("notifications", "Alerts", Icons.Default.Notifications)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

sealed class AuthScreen(val route: String) {
    data object Login : AuthScreen("login")
    data object Register : AuthScreen("register")
}

val bottomNavItems = listOf(Screen.Map, Screen.Friends, Screen.Notifications, Screen.Profile)

private val Context.dataStore by preferencesDataStore("trackme_prefs")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackMeNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Check for existing token on startup — auto-login if valid session exists
    LaunchedEffect(Unit) {
        try {
            val accessKey = stringPreferencesKey("access_token")
            val token = context.dataStore.data.first()[accessKey]
            if (!token.isNullOrBlank()) {
                isLoggedIn = true
                navController.navigate(Screen.Map.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } catch (_: Exception) {
            // DataStore unavailable — stay on login screen
        }
    }

    val showBottomBar = isLoggedIn && bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AuthScreen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(AuthScreen.Login.route) {
                LoginScreen(
                    onNavigateRegister = { navController.navigate(AuthScreen.Register.route) },
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate(Screen.Map.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(AuthScreen.Register.route) {
                RegisterScreen(
                    onNavigateLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        isLoggedIn = true
                        navController.navigate(Screen.Map.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Main screens
            composable(Screen.Map.route) {
                MapScreen()
            }
            composable(Screen.Friends.route) {
                FriendsScreen()
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

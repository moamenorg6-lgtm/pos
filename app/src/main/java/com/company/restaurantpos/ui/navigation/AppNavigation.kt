package com.company.restaurantpos.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.ui.components.BottomNavigationBar
import com.company.restaurantpos.ui.screens.*
import com.company.restaurantpos.ui.viewmodels.AuthViewModel
import com.company.restaurantpos.utils.LocalizationManager
import com.company.restaurantpos.utils.RoleBasedNavigation
import com.company.restaurantpos.utils.RoleGuard

/**
 * Main app navigation with authentication and role-based access control
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    localizationManager: LocalizationManager,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    
    // Handle navigation based on authentication state
    LaunchedEffect(authUiState.isLoggedIn) {
        if (!authUiState.isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // Determine start destination
    val startDestination = if (authUiState.isLoggedIn) {
        currentUser?.let { user ->
            RoleBasedNavigation.getDefaultRoute(user.role)
        } ?: Screen.Home.route
    } else {
        Screen.Splash.route
    }
    
    val user = currentUser
    if (authUiState.isLoggedIn && user != null) {
        // Main app with bottom navigation
        MainAppNavigation(
            navController = navController,
            currentUser = user,
            localizationManager = localizationManager,
            onLogout = { authViewModel.logout() }
        )
    } else {
        // Authentication flow
        AuthenticationNavigation(
            navController = navController,
            startDestination = startDestination,
            localizationManager = localizationManager,
            onLoginSuccess = {
                // Navigation will be handled by LaunchedEffect above
            }
        )
    }
}

/**
 * Main app navigation with bottom navigation bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppNavigation(
    navController: NavHostController,
    currentUser: com.company.restaurantpos.data.local.entities.User,
    localizationManager: LocalizationManager,
    onLogout: () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Get available screens for current user
    val availableScreens = getBottomNavigationScreens(currentUser.role.permissions)
    
    Scaffold(
        bottomBar = {
            if (availableScreens.isNotEmpty() && currentRoute in availableScreens.map { it.route }) {
                BottomNavigationBar(
                    screens = availableScreens,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = RoleBasedNavigation.getDefaultRoute(currentUser.role),
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home Screen
            composable(Screen.Home.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_POS,
                    content = {
                        HomeScreen(navController)
                    }
                )
            }
            
            // POS Screen
            composable(Screen.POS.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_POS,
                    content = { POSScreen() }
                )
            }
            
            // Kitchen Screen
            composable(Screen.Kitchen.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_KITCHEN_TICKETS,
                    content = { KitchenScreen() }
                )
            }
            
            // Reports Screen
            composable(Screen.Reports.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_REPORTS,
                    content = { ReportsScreen() }
                )
            }
            
            // Inventory Screen (placeholder)
            composable(Screen.Inventory.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_INVENTORY,
                    content = { InventoryScreen() }
                )
            }
            
            // Settings Screen
            composable(Screen.Settings.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.VIEW_SETTINGS,
                    content = { SettingsScreen() }
                )
            }
            
            // Admin Screen
            composable(Screen.Admin.route) {
                RoleGuard(
                    requiredPermission = com.company.restaurantpos.data.local.entities.Permission.MANAGE_USERS,
                    content = { AdminScreen() }
                )
            }
        }
    }
}

/**
 * Authentication navigation flow
 */
@Composable
private fun AuthenticationNavigation(
    navController: NavHostController,
    startDestination: String,
    localizationManager: LocalizationManager,
    onLoginSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = onLoginSuccess,
                localizationManager = localizationManager
            )
        }
        
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = onLoginSuccess
            )
        }
    }
}

/**
 * Placeholder Inventory Screen
 */
@Composable
private fun InventoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inventory Management",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Coming Soon",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
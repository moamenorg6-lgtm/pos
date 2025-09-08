package com.company.restaurantpos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.company.restaurantpos.data.local.entities.Permission

/**
 * Navigation screens with role-based access control
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val requiredPermissions: Set<Permission> = emptySet()
) {
    object Splash : Screen("splash", "Splash", Icons.Default.Restaurant)
    object Login : Screen("login", "Login", Icons.Default.Login)
    
    object Home : Screen(
        "home", 
        "Home", 
        Icons.Default.Home,
        setOf(Permission.VIEW_POS)
    )
    
    object POS : Screen(
        "pos", 
        "POS", 
        Icons.Default.PointOfSale,
        setOf(Permission.VIEW_POS)
    )
    
    object Kitchen : Screen(
        "kitchen", 
        "Kitchen", 
        Icons.Default.Restaurant,
        setOf(Permission.VIEW_KITCHEN_TICKETS)
    )
    
    object Reports : Screen(
        "reports", 
        "Reports", 
        Icons.Default.Assessment,
        setOf(Permission.VIEW_REPORTS)
    )
    
    object Inventory : Screen(
        "inventory", 
        "Inventory", 
        Icons.Default.Inventory,
        setOf(Permission.VIEW_INVENTORY)
    )
    
    object Settings : Screen(
        "settings", 
        "Settings", 
        Icons.Default.Settings,
        setOf(Permission.VIEW_SETTINGS)
    )
    
    object Admin : Screen(
        "admin", 
        "Admin", 
        Icons.Default.AdminPanelSettings,
        setOf(Permission.MANAGE_USERS)
    )
    
    object Users : Screen(
        "users", 
        "Users", 
        Icons.Default.People,
        setOf(Permission.MANAGE_USERS)
    )
    
    object Backup : Screen(
        "backup", 
        "Backup", 
        Icons.Default.Backup,
        setOf(Permission.BACKUP_RESTORE)
    )
}

/**
 * Navigation routes that don't require authentication
 */
val publicRoutes = setOf(
    Screen.Splash.route,
    Screen.Login.route
)

/**
 * Get main navigation screens based on user permissions
 */
fun getMainNavigationScreens(userPermissions: Set<Permission>): List<Screen> {
    return listOf(
        Screen.Home,
        Screen.POS,
        Screen.Kitchen,
        Screen.Reports,
        Screen.Inventory,
        Screen.Settings,
        Screen.Admin
    ).filter { screen ->
        screen.requiredPermissions.isEmpty() || 
        screen.requiredPermissions.any { permission -> userPermissions.contains(permission) }
    }
}

/**
 * Get bottom navigation screens (most commonly used)
 */
fun getBottomNavigationScreens(userPermissions: Set<Permission>): List<Screen> {
    return listOf(
        Screen.Home,
        Screen.POS,
        Screen.Kitchen,
        Screen.Reports,
        Screen.Settings
    ).filter { screen ->
        screen.requiredPermissions.isEmpty() || 
        screen.requiredPermissions.any { permission -> userPermissions.contains(permission) }
    }
}
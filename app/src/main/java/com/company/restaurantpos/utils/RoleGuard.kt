package com.company.restaurantpos.utils

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.restaurantpos.data.local.entities.Permission
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.ui.viewmodels.AuthViewModel

/**
 * Role-based access control guard for navigation and UI components
 */
@Composable
fun RoleGuard(
    requiredPermission: Permission,
    onUnauthorized: @Composable () -> Unit = { UnauthorizedAccess() },
    content: @Composable () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    
    if (currentUser != null && currentUser.role.permissions.contains(requiredPermission)) {
        content()
    } else {
        onUnauthorized()
    }
}

/**
 * Role-based access control guard for multiple permissions (requires ALL)
 */
@Composable
fun RoleGuardAll(
    requiredPermissions: Set<Permission>,
    onUnauthorized: @Composable () -> Unit = { UnauthorizedAccess() },
    content: @Composable () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    
    val hasAllPermissions = currentUser?.let { user ->
        requiredPermissions.all { permission ->
            user.role.permissions.contains(permission)
        }
    } ?: false
    
    if (hasAllPermissions) {
        content()
    } else {
        onUnauthorized()
    }
}

/**
 * Role-based access control guard for multiple permissions (requires ANY)
 */
@Composable
fun RoleGuardAny(
    requiredPermissions: Set<Permission>,
    onUnauthorized: @Composable () -> Unit = { UnauthorizedAccess() },
    content: @Composable () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    
    val hasAnyPermission = currentUser?.let { user ->
        requiredPermissions.any { permission ->
            user.role.permissions.contains(permission)
        }
    } ?: false
    
    if (hasAnyPermission) {
        content()
    } else {
        onUnauthorized()
    }
}

/**
 * Role-based access control guard for specific roles
 */
@Composable
fun RoleGuardRole(
    requiredRoles: Set<UserRole>,
    onUnauthorized: @Composable () -> Unit = { UnauthorizedAccess() },
    content: @Composable () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    
    if (currentUser != null && requiredRoles.contains(currentUser.role)) {
        content()
    } else {
        onUnauthorized()
    }
}

/**
 * Check if current user has permission (non-composable)
 */
suspend fun hasPermission(permission: Permission, authViewModel: AuthViewModel): Boolean {
    return authViewModel.hasPermission(permission)
}

/**
 * Check if current user has any of the permissions (non-composable)
 */
suspend fun hasAnyPermission(permissions: Set<Permission>, authViewModel: AuthViewModel): Boolean {
    return permissions.any { authViewModel.hasPermission(it) }
}

/**
 * Check if current user has all permissions (non-composable)
 */
suspend fun hasAllPermissions(permissions: Set<Permission>, authViewModel: AuthViewModel): Boolean {
    return permissions.all { authViewModel.hasPermission(it) }
}

/**
 * Default unauthorized access component
 */
@Composable
private fun UnauthorizedAccess() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                contentDescription = "Access Denied",
                modifier = androidx.compose.ui.Modifier.size(64.dp),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
            
            androidx.compose.material3.Text(
                text = "Access Denied",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
            
            androidx.compose.material3.Text(
                text = "You don't have permission to access this feature",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Navigation route permissions mapping
 */
object RoutePermissions {
    val POS = setOf(Permission.VIEW_POS)
    val REPORTS = setOf(Permission.VIEW_REPORTS)
    val INVENTORY = setOf(Permission.VIEW_INVENTORY)
    val SETTINGS = setOf(Permission.VIEW_SETTINGS)
    val USER_MANAGEMENT = setOf(Permission.MANAGE_USERS)
    val BACKUP_RESTORE = setOf(Permission.BACKUP_RESTORE)
    val KITCHEN_TICKETS = setOf(Permission.VIEW_KITCHEN_TICKETS)
    val PRINT_RECEIPTS = setOf(Permission.PRINT_RECEIPTS)
    val PRINT_KITCHEN_TICKETS = setOf(Permission.PRINT_KITCHEN_TICKETS)
}

/**
 * Role-based navigation helper
 */
object RoleBasedNavigation {
    
    /**
     * Get available routes for current user role
     */
    fun getAvailableRoutes(userRole: UserRole): List<String> {
        return when (userRole) {
            UserRole.ADMIN -> listOf(
                "pos", "reports", "inventory", "settings", "users", "backup"
            )
            UserRole.CASHIER -> listOf(
                "pos"
            )
            UserRole.KITCHEN -> listOf(
                "kitchen"
            )
        }
    }
    
    /**
     * Check if route is accessible for user role
     */
    fun isRouteAccessible(route: String, userRole: UserRole): Boolean {
        return getAvailableRoutes(userRole).contains(route)
    }
    
    /**
     * Get default route for user role
     */
    fun getDefaultRoute(userRole: UserRole): String {
        return when (userRole) {
            UserRole.ADMIN -> "pos"
            UserRole.CASHIER -> "pos"
            UserRole.KITCHEN -> "kitchen"
        }
    }
}
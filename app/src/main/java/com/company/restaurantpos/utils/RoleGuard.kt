package com.company.restaurantpos.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    
    currentUser?.let { user ->
        if (user.role.permissions.contains(requiredPermission)) {
            content()
        } else {
            onUnauthorized()
        }
    } ?: onUnauthorized()
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
    
    currentUser?.let { user ->
        if (requiredRoles.contains(user.role)) {
            content()
        } else {
            onUnauthorized()
        }
    } ?: onUnauthorized()
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Access Denied",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = "Access Denied",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = "You don't have permission to access this feature",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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
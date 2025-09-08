package com.company.restaurantpos.utils

import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.ui.navigation.Screen

/**
 * Utility for role-based navigation routing
 */
object RoleBasedNavigation {
    
    /**
     * Get the default route for a user role
     */
    fun getDefaultRoute(role: UserRole): String {
        return when (role) {
            UserRole.ADMIN -> Screen.Home.route
            UserRole.CASHIER -> Screen.POS.route
            UserRole.KITCHEN -> Screen.Kitchen.route
        }
    }
    
    /**
     * Get the home screen for a user role
     */
    fun getHomeScreen(role: UserRole): Screen {
        return when (role) {
            UserRole.ADMIN -> Screen.Home
            UserRole.CASHIER -> Screen.POS
            UserRole.KITCHEN -> Screen.Kitchen
        }
    }
    
    /**
     * Check if a user role can access a specific route
     */
    fun canAccessRoute(role: UserRole, route: String): Boolean {
        val userPermissions = role.permissions
        
        return when (route) {
            Screen.Splash.route, Screen.Login.route -> true // Public routes
            Screen.Home.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_POS)
            Screen.POS.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_POS)
            Screen.Kitchen.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_KITCHEN_TICKETS)
            Screen.Reports.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_REPORTS)
            Screen.Inventory.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_INVENTORY)
            Screen.Settings.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.VIEW_SETTINGS)
            Screen.Admin.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.MANAGE_USERS)
            Screen.Users.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.MANAGE_USERS)
            Screen.Backup.route -> userPermissions.contains(com.company.restaurantpos.data.local.entities.Permission.BACKUP_RESTORE)
            else -> false
        }
    }
    
    /**
     * Get available routes for a user role
     */
    fun getAvailableRoutes(role: UserRole): List<String> {
        val allRoutes = listOf(
            Screen.Home.route,
            Screen.POS.route,
            Screen.Kitchen.route,
            Screen.Reports.route,
            Screen.Inventory.route,
            Screen.Settings.route,
            Screen.Admin.route,
            Screen.Users.route,
            Screen.Backup.route
        )
        
        return allRoutes.filter { route -> canAccessRoute(role, route) }
    }
    
    /**
     * Get redirect route if user tries to access unauthorized route
     */
    fun getRedirectRoute(role: UserRole, attemptedRoute: String): String {
        return if (canAccessRoute(role, attemptedRoute)) {
            attemptedRoute
        } else {
            getDefaultRoute(role)
        }
    }
}
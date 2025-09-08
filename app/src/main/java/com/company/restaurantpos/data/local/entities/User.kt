package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User entity representing system users (staff)
 * 
 * @property id Unique user identifier (auto-generated)
 * @property username Unique username for login
 * @property passwordHash Hashed password for security (SHA-256)
 * @property role User role with specific permissions
 * @property isActive Whether the user account is active
 * @property createdAt Timestamp when user was created
 * @property lastLoginAt Timestamp of last successful login
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val username: String,
    
    val passwordHash: String,
    
    val role: UserRole,
    
    val isActive: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val lastLoginAt: Long? = null
)

enum class UserRole(val displayName: String, val permissions: Set<Permission>) {
    ADMIN("Admin", setOf(
        Permission.VIEW_POS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_INVENTORY,
        Permission.VIEW_SETTINGS,
        Permission.MANAGE_USERS,
        Permission.BACKUP_RESTORE,
        Permission.PRINT_RECEIPTS,
        Permission.PRINT_KITCHEN_TICKETS
    )),
    CASHIER("Cashier", setOf(
        Permission.VIEW_POS,
        Permission.PRINT_RECEIPTS,
        Permission.PRINT_KITCHEN_TICKETS
    )),
    KITCHEN("Kitchen", setOf(
        Permission.VIEW_KITCHEN_TICKETS
    ))
}

enum class Permission {
    VIEW_POS,
    VIEW_REPORTS,
    VIEW_INVENTORY,
    VIEW_SETTINGS,
    MANAGE_USERS,
    BACKUP_RESTORE,
    PRINT_RECEIPTS,
    PRINT_KITCHEN_TICKETS,
    VIEW_KITCHEN_TICKETS
}
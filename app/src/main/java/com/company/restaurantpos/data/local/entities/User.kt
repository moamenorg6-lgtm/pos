package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User entity representing system users (staff)
 * 
 * @property id Unique user identifier (auto-generated)
 * @property username Unique username for login
 * @property passwordHash Hashed password for security
 * @property role User role: "admin", "cashier", "kitchen"
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
    
    val role: String // admin, cashier, kitchen
)
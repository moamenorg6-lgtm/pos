package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity
 * Provides methods for user-related database operations
 */
@Dao
interface UserDao {
    
    /**
     * Get all active users
     */
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY username ASC")
    suspend fun getAllUsers(): List<User>
    
    /**
     * Get all users as Flow for reactive updates
     */
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY username ASC")
    fun getAllUsersFlow(): Flow<List<User>>
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE id = :userId AND isActive = 1")
    suspend fun getById(userId: Int): User?
    
    /**
     * Get user by username
     * @param username Username
     * @return User if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username AND isActive = 1")
    suspend fun getByUsername(username: String): User?
    
    /**
     * Get users by role
     * @param role User role
     * @return List of users with the specified role
     */
    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1 ORDER BY username ASC")
    suspend fun getByRole(role: UserRole): List<User>
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return True if username exists, false otherwise
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username AND isActive = 1")
    suspend fun usernameExists(username: String): Boolean
    
    /**
     * Validate user credentials for login
     * @param username Username
     * @param passwordHash Hashed password
     * @return User if credentials are valid and user is active, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash AND isActive = 1")
    suspend fun validateCredentials(username: String, passwordHash: String): User?
    
    /**
     * Update user's last login timestamp
     * @param userId User ID
     * @param timestamp Login timestamp
     */
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Int, timestamp: Long)
    
    /**
     * Update user password
     * @param userId User ID
     * @param newPasswordHash New hashed password
     * @return Number of rows updated
     */
    @Query("UPDATE users SET passwordHash = :newPasswordHash WHERE id = :userId")
    suspend fun updatePassword(userId: Int, newPasswordHash: String): Int
    
    /**
     * Update user role
     * @param userId User ID
     * @param newRole New role
     * @return Number of rows updated
     */
    @Query("UPDATE users SET role = :newRole WHERE id = :userId")
    suspend fun updateRole(userId: Int, newRole: UserRole): Int
    
    /**
     * Deactivate user (soft delete)
     * @param userId User ID
     * @return Number of rows updated
     */
    @Query("UPDATE users SET isActive = 0 WHERE id = :userId")
    suspend fun deactivateUser(userId: Int): Int
    
    /**
     * Activate user
     * @param userId User ID
     * @return Number of rows updated
     */
    @Query("UPDATE users SET isActive = 1 WHERE id = :userId")
    suspend fun activateUser(userId: Int): Int
    
    /**
     * Get count of users by role
     * @param role User role
     * @return Count of active users with the specified role
     */
    @Query("SELECT COUNT(*) FROM users WHERE role = :role AND isActive = 1")
    suspend fun getCountByRole(role: UserRole): Int
    
    /**
     * Insert a new user
     * @param user User to insert
     * @return Row ID of inserted user
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long
    
    /**
     * Update existing user
     * @param user User to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(user: User): Int
    
    /**
     * Delete user (hard delete - use deactivateUser for soft delete)
     * @param user User to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(user: User): Int
    
    /**
     * Delete user by ID (hard delete)
     * @param userId User ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Int): Int
    
    /**
     * Delete all users
     * @return Number of rows deleted
     */
    @Query("DELETE FROM users")
    suspend fun deleteAll(): Int
}
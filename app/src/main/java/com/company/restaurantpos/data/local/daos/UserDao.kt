package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.User

/**
 * Data Access Object for User entity
 * Provides methods for user-related database operations
 */
@Dao
interface UserDao {
    
    /**
     * Get all users
     */
    @Query("SELECT * FROM users ORDER BY username ASC")
    suspend fun getAllUsers(): List<User>
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getById(userId: Int): User?
    
    /**
     * Get user by username
     * @param username Username
     * @return User if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getByUsername(username: String): User?
    
    /**
     * Get users by role
     * @param role User role (admin, cashier, kitchen)
     * @return List of users with the specified role
     */
    @Query("SELECT * FROM users WHERE role = :role ORDER BY username ASC")
    suspend fun getByRole(role: String): List<User>
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return True if username exists, false otherwise
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username")
    suspend fun usernameExists(username: String): Boolean
    
    /**
     * Validate user credentials
     * @param username Username
     * @param passwordHash Hashed password
     * @return User if credentials are valid, null otherwise
     */
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash")
    suspend fun validateCredentials(username: String, passwordHash: String): User?
    
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
    suspend fun updateRole(userId: Int, newRole: String): Int
    
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
     * Delete user
     * @param user User to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(user: User): Int
    
    /**
     * Delete user by ID
     * @param userId User ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Int): Int
}
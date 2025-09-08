package com.company.restaurantpos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.company.restaurantpos.data.local.daos.UserDao
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.utils.PasswordUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

/**
 * Repository for authentication and user session management
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val context: Context
) {
    
    companion object {
        private val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_LOGIN = booleanPreferencesKey("remember_login")
    }
    
    /**
     * Login with username and password
     * @param username Username
     * @param password Plain text password
     * @param rememberMe Whether to remember login
     * @return LoginResult with success status and user data
     */
    suspend fun login(username: String, password: String, rememberMe: Boolean = false): LoginResult {
        return try {
            // Input validation
            if (username.isBlank() || password.isBlank()) {
                return LoginResult.Error("Username and password are required")
            }
            
            // Get user by username
            val user = userDao.getByUsername(username.trim())
                ?: return LoginResult.Error("Invalid username or password")
            
            // Verify password
            if (!PasswordUtils.verifyPassword(password, user.passwordHash)) {
                return LoginResult.Error("Invalid username or password")
            }
            
            // Check if user is active
            if (!user.isActive) {
                return LoginResult.Error("Account is deactivated. Please contact administrator.")
            }
            
            // Update last login timestamp
            userDao.updateLastLogin(user.id, System.currentTimeMillis())
            
            // Save session
            saveUserSession(user.id, rememberMe)
            
            LoginResult.Success(user)
        } catch (e: Exception) {
            LoginResult.Error("Login failed: ${e.message}")
        }
    }
    
    /**
     * Logout current user
     */
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Get current logged-in user
     * @return Current user or null if not logged in
     */
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId()
        return if (userId != null) {
            userDao.getById(userId)
        } else null
    }
    
    /**
     * Get current user as Flow for reactive updates
     */
    fun getCurrentUserFlow(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val userId = preferences[CURRENT_USER_ID]
            if (userId != null) {
                userDao.getById(userId)
            } else null
        }
    }
    
    /**
     * Check if user is currently logged in
     */
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
        val userId = preferences[CURRENT_USER_ID]
        
        // Verify user still exists and is active
        if (isLoggedIn && userId != null) {
            val user = userDao.getById(userId)
            return user != null && user.isActive
        }
        
        return false
    }
    
    /**
     * Get current user ID from session
     */
    private suspend fun getCurrentUserId(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[CURRENT_USER_ID]
    }
    
    /**
     * Save user session to DataStore
     */
    private suspend fun saveUserSession(userId: Int, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
            preferences[IS_LOGGED_IN] = true
            preferences[REMEMBER_LOGIN] = rememberMe
        }
    }
    
    /**
     * Check if user has specific permission
     * @param permission Permission to check
     * @return True if user has permission, false otherwise
     */
    suspend fun hasPermission(permission: com.company.restaurantpos.data.local.entities.Permission): Boolean {
        val user = getCurrentUser() ?: return false
        return user.role.permissions.contains(permission)
    }
    
    /**
     * Create a new user (admin only)
     * @param username Username
     * @param password Plain text password
     * @param role User role
     * @return Result of user creation
     */
    suspend fun createUser(username: String, password: String, role: UserRole): CreateUserResult {
        return try {
            // Check if current user is admin
            val currentUser = getCurrentUser()
            if (currentUser?.role != UserRole.ADMIN) {
                return CreateUserResult.Error("Only administrators can create users")
            }
            
            // Validate input
            if (username.isBlank()) {
                return CreateUserResult.Error("Username is required")
            }
            
            val passwordErrors = PasswordUtils.validatePasswordStrength(password)
            if (passwordErrors.isNotEmpty()) {
                return CreateUserResult.Error(passwordErrors.first())
            }
            
            // Check if username already exists
            if (userDao.usernameExists(username.trim())) {
                return CreateUserResult.Error("Username already exists")
            }
            
            // Hash password and create user
            val hashedPassword = PasswordUtils.hashPassword(password)
            val user = User(
                username = username.trim(),
                passwordHash = hashedPassword,
                role = role
            )
            
            val userId = userDao.insert(user)
            CreateUserResult.Success(userId)
        } catch (e: Exception) {
            CreateUserResult.Error("Failed to create user: ${e.message}")
        }
    }
    
    /**
     * Change user password
     * @param currentPassword Current password
     * @param newPassword New password
     * @return Result of password change
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): ChangePasswordResult {
        return try {
            val user = getCurrentUser() ?: return ChangePasswordResult.Error("Not logged in")
            
            // Verify current password
            if (!PasswordUtils.verifyPassword(currentPassword, user.passwordHash)) {
                return ChangePasswordResult.Error("Current password is incorrect")
            }
            
            // Validate new password
            val passwordErrors = PasswordUtils.validatePasswordStrength(newPassword)
            if (passwordErrors.isNotEmpty()) {
                return ChangePasswordResult.Error(passwordErrors.first())
            }
            
            // Hash new password and update
            val hashedPassword = PasswordUtils.hashPassword(newPassword)
            val rowsUpdated = userDao.updatePassword(user.id, hashedPassword)
            
            if (rowsUpdated > 0) {
                ChangePasswordResult.Success
            } else {
                ChangePasswordResult.Error("Failed to update password")
            }
        } catch (e: Exception) {
            ChangePasswordResult.Error("Failed to change password: ${e.message}")
        }
    }
    
    /**
     * Initialize default admin user if no users exist
     */
    suspend fun initializeDefaultUser() {
        try {
            val userCount = userDao.getCountByRole(UserRole.ADMIN)
            if (userCount == 0) {
                val defaultPassword = "admin123"
                val hashedPassword = PasswordUtils.hashPassword(defaultPassword)
                val adminUser = User(
                    username = "admin",
                    passwordHash = hashedPassword,
                    role = UserRole.ADMIN
                )
                userDao.insert(adminUser)
            }
        } catch (e: Exception) {
            // Log error but don't throw - app should still work
        }
    }
}

/**
 * Result of login attempt
 */
sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

/**
 * Result of user creation
 */
sealed class CreateUserResult {
    data class Success(val userId: Long) : CreateUserResult()
    data class Error(val message: String) : CreateUserResult()
}

/**
 * Result of password change
 */
sealed class ChangePasswordResult {
    object Success : ChangePasswordResult()
    data class Error(val message: String) : ChangePasswordResult()
}
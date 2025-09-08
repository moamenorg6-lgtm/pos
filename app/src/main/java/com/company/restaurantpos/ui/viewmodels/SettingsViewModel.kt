package com.company.restaurantpos.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.restaurantpos.data.backup.*
import com.company.restaurantpos.data.local.daos.UserDao
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val userDao: UserDao
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    // Backup options
    private var backupIncludeUsers = true
    private var backupEncryptionKey: String? = null
    
    // Restore options
    private var restoreEncryptionKey: String? = null
    private var restoreReplaceExisting = false
    
    init {
        loadUserCount()
    }
    
    /**
     * Load user count
     */
    private fun loadUserCount() {
        viewModelScope.launch {
            try {
                val adminCount = userDao.getCountByRole(UserRole.ADMIN)
                val cashierCount = userDao.getCountByRole(UserRole.CASHIER)
                val kitchenCount = userDao.getCountByRole(UserRole.KITCHEN)
                val totalCount = adminCount + cashierCount + kitchenCount
                
                _uiState.value = _uiState.value.copy(
                    userCount = totalCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load user count: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Load all users
     */
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val users = userDao.getAllUsers()
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load users: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Deactivate user
     */
    fun deactivateUser(userId: Int) {
        viewModelScope.launch {
            try {
                userDao.deactivateUser(userId)
                loadUsers() // Refresh list
                _uiState.value = _uiState.value.copy(
                    successMessage = "User deactivated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to deactivate user: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Change user role
     */
    fun changeUserRole(userId: Int, newRole: UserRole) {
        viewModelScope.launch {
            try {
                userDao.updateRole(userId, newRole)
                loadUsers() // Refresh list
                _uiState.value = _uiState.value.copy(
                    successMessage = "User role updated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update user role: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Set backup options
     */
    fun setBackupOptions(includeUsers: Boolean, encryptionKey: String?) {
        backupIncludeUsers = includeUsers
        backupEncryptionKey = encryptionKey
    }
    
    /**
     * Set restore options
     */
    fun setRestoreOptions(encryptionKey: String?, replaceExisting: Boolean) {
        restoreEncryptionKey = encryptionKey
        restoreReplaceExisting = replaceExisting
    }
    
    /**
     * Export backup to file
     */
    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = backupManager.exportToFile(
                uri = uri,
                includeUsers = backupIncludeUsers,
                encryptionKey = backupEncryptionKey
            )
            
            when (result) {
                is ExportResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Backup exported successfully",
                        lastBackupTime = result.timestamp
                    )
                }
                is ExportResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Import backup from file
     */
    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = backupManager.importFromFile(
                uri = uri,
                encryptionKey = restoreEncryptionKey,
                replaceExisting = restoreReplaceExisting
            )
            
            when (result) {
                is ImportResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Backup imported successfully"
                    )
                    loadUserCount() // Refresh user count
                }
                is ImportResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Generate backup filename
     */
    fun generateBackupFilename(): String {
        return backupManager.generateBackupFilename()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

/**
 * UI State for settings screen
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val userCount: Int = 0,
    val lastBackupTime: Long? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
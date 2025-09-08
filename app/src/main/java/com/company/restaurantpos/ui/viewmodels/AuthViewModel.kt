package com.company.restaurantpos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.data.repository.AuthRepository
import com.company.restaurantpos.data.repository.ChangePasswordResult
import com.company.restaurantpos.data.repository.CreateUserResult
import com.company.restaurantpos.data.repository.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication and user management
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Current user
    val currentUser: Flow<User?> = authRepository.getCurrentUserFlow()
    
    // Login state
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    init {
        // Initialize default admin user
        viewModelScope.launch {
            authRepository.initializeDefaultUser()
        }
        
        // Check if user is already logged in
        checkLoginStatus()
    }
    
    /**
     * Login with username and password
     */
    fun login(username: String, password: String, rememberMe: Boolean = false) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val result = authRepository.login(username, password, rememberMe)
            _loginState.value = when (result) {
                is LoginResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        currentUser = result.user,
                        errorMessage = null
                    )
                    LoginState.Success(result.user)
                }
                is LoginResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message
                    )
                    LoginState.Error(result.message)
                }
            }
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
            _loginState.value = LoginState.Idle
        }
    }
    
    /**
     * Check current login status
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            val currentUser = if (isLoggedIn) authRepository.getCurrentUser() else null
            
            _uiState.value = _uiState.value.copy(
                isLoggedIn = isLoggedIn,
                currentUser = currentUser
            )
            
            if (isLoggedIn && currentUser != null) {
                _loginState.value = LoginState.Success(currentUser)
            }
        }
    }
    
    /**
     * Create a new user (admin only)
     */
    fun createUser(username: String, password: String, role: UserRole, onResult: (CreateUserResult) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = authRepository.createUser(username, password, role)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (result is CreateUserResult.Error) result.message else null
            )
            
            onResult(result)
        }
    }
    
    /**
     * Change current user's password
     */
    fun changePassword(currentPassword: String, newPassword: String, onResult: (ChangePasswordResult) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = authRepository.changePassword(currentPassword, newPassword)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (result is ChangePasswordResult.Error) result.message else null
            )
            
            onResult(result)
        }
    }
    
    /**
     * Check if current user has specific permission
     */
    suspend fun hasPermission(permission: com.company.restaurantpos.data.local.entities.Permission): Boolean {
        return authRepository.hasPermission(permission)
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Update login form state
     */
    fun updateLoginForm(username: String, password: String, rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(
                username = username,
                password = password,
                rememberMe = rememberMe
            )
        )
    }
    
    /**
     * Validate login form
     */
    fun validateLoginForm(): Boolean {
        val form = _uiState.value.loginForm
        val errors = mutableListOf<String>()
        
        if (form.username.isBlank()) {
            errors.add("Username is required")
        }
        
        if (form.password.isBlank()) {
            errors.add("Password is required")
        }
        
        _uiState.value = _uiState.value.copy(
            loginFormErrors = errors
        )
        
        return errors.isEmpty()
    }
}

/**
 * UI State for authentication
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginForm: LoginFormState = LoginFormState(),
    val loginFormErrors: List<String> = emptyList()
)

/**
 * Login form state
 */
data class LoginFormState(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false
)

/**
 * Login state
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
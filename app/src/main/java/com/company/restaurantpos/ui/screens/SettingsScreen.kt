package com.company.restaurantpos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.restaurantpos.data.backup.*
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.ui.viewmodels.AuthViewModel
import com.company.restaurantpos.ui.viewmodels.SettingsViewModel
import com.company.restaurantpos.utils.RoleGuard
import com.company.restaurantpos.data.local.entities.Permission
import kotlinx.coroutines.launch

/**
 * Settings screen with backup/restore and user management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showCreateUserDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    
    // File picker launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { settingsViewModel.exportBackup(it) }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { settingsViewModel.importBackup(it) }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Management Section
            item {
                RoleGuard(requiredPermission = Permission.MANAGE_USERS) {
                    SettingsSection(
                        title = "User Management",
                        icon = Icons.Default.People
                    ) {
                        SettingsItem(
                            title = "Create New User",
                            subtitle = "Add a new user account",
                            icon = Icons.Default.PersonAdd,
                            onClick = { showCreateUserDialog = true }
                        )
                        
                        SettingsItem(
                            title = "Manage Users",
                            subtitle = "${uiState.userCount} active users",
                            icon = Icons.Default.ManageAccounts,
                            onClick = { settingsViewModel.loadUsers() }
                        )
                    }
                }
            }
            
            // Account Section
            item {
                SettingsSection(
                    title = "Account",
                    icon = Icons.Default.AccountCircle
                ) {
                    SettingsItem(
                        title = "Change Password",
                        subtitle = "Update your account password",
                        icon = Icons.Default.Lock,
                        onClick = { showChangePasswordDialog = true }
                    )
                    
                    SettingsItem(
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        icon = Icons.Default.Logout,
                        onClick = { authViewModel.logout() }
                    )
                }
            }
            
            // Backup & Restore Section
            item {
                RoleGuard(requiredPermission = Permission.BACKUP_RESTORE) {
                    SettingsSection(
                        title = "Backup & Restore",
                        icon = Icons.Default.Backup
                    ) {
                        SettingsItem(
                            title = "Export Backup",
                            subtitle = "Save your data to file",
                            icon = Icons.Default.FileDownload,
                            onClick = { showBackupDialog = true }
                        )
                        
                        SettingsItem(
                            title = "Import Backup",
                            subtitle = "Restore data from file",
                            icon = Icons.Default.FileUpload,
                            onClick = { showRestoreDialog = true }
                        )
                        
                        if (uiState.lastBackupTime != null) {
                            Text(
                                text = "Last backup: ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(uiState.lastBackupTime))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 56.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
            
            // App Information Section
            item {
                SettingsSection(
                    title = "App Information",
                    icon = Icons.Default.Info
                ) {
                    SettingsItem(
                        title = "Version",
                        subtitle = "1.0.0",
                        icon = Icons.Default.AppSettingsAlt,
                        onClick = { }
                    )
                    
                    SettingsItem(
                        title = "About",
                        subtitle = "Restaurant POS System",
                        icon = Icons.Default.Restaurant,
                        onClick = { }
                    )
                }
            }
            
            // User List (if loaded)
            if (uiState.users.isNotEmpty()) {
                item {
                    RoleGuard(requiredPermission = Permission.MANAGE_USERS) {
                        SettingsSection(
                            title = "Active Users",
                            icon = Icons.Default.Group
                        ) {
                            // User list content
                        }
                    }
                }
                
                items(uiState.users) { user ->
                    UserListItem(
                        user = user,
                        currentUserId = currentUser?.id ?: 0,
                        onDeactivate = { settingsViewModel.deactivateUser(user.id) },
                        onChangeRole = { newRole -> settingsViewModel.changeUserRole(user.id, newRole) }
                    )
                }
            }
        }
    }
    
    // Loading indicator
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Error snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            settingsViewModel.clearError()
        }
    }
    
    // Success message
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show success snackbar
            settingsViewModel.clearSuccess()
        }
    }
    
    // Dialogs
    if (showCreateUserDialog) {
        CreateUserDialog(
            onDismiss = { showCreateUserDialog = false },
            onCreateUser = { username, password, role ->
                authViewModel.createUser(username, password, role) { result ->
                    when (result) {
                        is com.company.restaurantpos.data.repository.CreateUserResult.Success -> {
                            showCreateUserDialog = false
                            settingsViewModel.loadUsers()
                        }
                        is com.company.restaurantpos.data.repository.CreateUserResult.Error -> {
                            // Error handled by AuthViewModel
                        }
                    }
                }
            }
        )
    }
    
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onChangePassword = { currentPassword, newPassword ->
                authViewModel.changePassword(currentPassword, newPassword) { result ->
                    when (result) {
                        is com.company.restaurantpos.data.repository.ChangePasswordResult.Success -> {
                            showChangePasswordDialog = false
                        }
                        is com.company.restaurantpos.data.repository.ChangePasswordResult.Error -> {
                            // Error handled by AuthViewModel
                        }
                    }
                }
            }
        )
    }
    
    if (showBackupDialog) {
        BackupDialog(
            onDismiss = { showBackupDialog = false },
            onExport = { includeUsers, encryptionKey ->
                val filename = settingsViewModel.generateBackupFilename()
                exportLauncher.launch(filename)
                settingsViewModel.setBackupOptions(includeUsers, encryptionKey)
                showBackupDialog = false
            }
        )
    }
    
    if (showRestoreDialog) {
        RestoreDialog(
            onDismiss = { showRestoreDialog = false },
            onImport = { encryptionKey, replaceExisting ->
                settingsViewModel.setRestoreOptions(encryptionKey, replaceExisting)
                importLauncher.launch(arrayOf("application/json", "*/*"))
                showRestoreDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
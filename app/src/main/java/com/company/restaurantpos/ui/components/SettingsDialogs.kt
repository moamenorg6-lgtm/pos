package com.company.restaurantpos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole

/**
 * Dialog for creating a new user
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreateUser: (username: String, password: String, role: UserRole) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CASHIER) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val isValid = username.isNotBlank() && 
                  password.isNotBlank() && 
                  password == confirmPassword &&
                  password.length >= 6
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create New User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )
                
                // Confirm password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = confirmPassword.isNotEmpty() && password != confirmPassword
                )
                
                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Role selection
                Text(
                    text = "Role",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Column {
                    UserRole.values().forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedRole == role),
                                    onClick = { selectedRole = role },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedRole == role),
                                onClick = { selectedRole = role }
                            )
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = getRoleDescription(role),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            onCreateUser(username, password, selectedRole)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

/**
 * Dialog for changing password
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onChangePassword: (currentPassword: String, newPassword: String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val isValid = currentPassword.isNotBlank() && 
                  newPassword.isNotBlank() && 
                  newPassword == confirmPassword &&
                  newPassword.length >= 6
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Current password field
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(
                                imageVector = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (currentPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )
                
                // New password field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )
                
                // Confirm new password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword
                )
                
                if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            onChangePassword(currentPassword, newPassword)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid
                    ) {
                        Text("Change")
                    }
                }
            }
        }
    }
}

/**
 * Dialog for backup options
 */
@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onExport: (includeUsers: Boolean, encryptionKey: String?) -> Unit
) {
    var includeUsers by remember { mutableStateOf(true) }
    var useEncryption by remember { mutableStateOf(false) }
    var encryptionKey by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Export Backup",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Include users option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includeUsers,
                        onCheckedChange = { includeUsers = it }
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Include Users",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "User passwords will be excluded for security",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Encryption option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = useEncryption,
                        onCheckedChange = { useEncryption = it }
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Encrypt Backup",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Protect backup with encryption key",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Encryption key field
                if (useEncryption) {
                    OutlinedTextField(
                        value = encryptionKey,
                        onValueChange = { encryptionKey = it },
                        label = { Text("Encryption Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Enter a secure key") }
                    )
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val key = if (useEncryption && encryptionKey.isNotBlank()) encryptionKey else null
                            onExport(includeUsers, key)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !useEncryption || encryptionKey.isNotBlank()
                    ) {
                        Text("Export")
                    }
                }
            }
        }
    }
}

/**
 * Dialog for restore options
 */
@Composable
fun RestoreDialog(
    onDismiss: () -> Unit,
    onImport: (encryptionKey: String?, replaceExisting: Boolean) -> Unit
) {
    var useEncryption by remember { mutableStateOf(false) }
    var encryptionKey by remember { mutableStateOf("") }
    var replaceExisting by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Import Backup",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Encryption option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = useEncryption,
                        onCheckedChange = { useEncryption = it }
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Backup is Encrypted",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Provide decryption key",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Encryption key field
                if (useEncryption) {
                    OutlinedTextField(
                        value = encryptionKey,
                        onValueChange = { encryptionKey = it },
                        label = { Text("Decryption Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Enter the encryption key") }
                    )
                }
                
                // Replace existing option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = replaceExisting,
                        onCheckedChange = { replaceExisting = it }
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Replace Existing Data",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Clear current data before import",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (replaceExisting) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Warning: This will permanently delete all existing data!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val key = if (useEncryption && encryptionKey.isNotBlank()) encryptionKey else null
                            onImport(key, replaceExisting)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !useEncryption || encryptionKey.isNotBlank()
                    ) {
                        Text("Import")
                    }
                }
            }
        }
    }
}

/**
 * User list item component
 */
@Composable
fun UserListItem(
    user: User,
    currentUserId: Int,
    onDeactivate: () -> Unit,
    onChangeRole: (UserRole) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = user.role.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (user.lastLoginAt != null) {
                    Text(
                        text = "Last login: ${java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(user.lastLoginAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (user.id != currentUserId) {
                OutlinedButton(
                    onClick = onDeactivate,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Deactivate")
                }
            }
        }
    }
}

/**
 * Get role description
 */
private fun getRoleDescription(role: UserRole): String {
    return when (role) {
        UserRole.ADMIN -> "Full access to all features"
        UserRole.CASHIER -> "POS and checkout access"
        UserRole.KITCHEN -> "Kitchen tickets only"
    }
}
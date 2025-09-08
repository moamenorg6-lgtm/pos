package com.company.restaurantpos.data.backup

import android.content.Context
import android.net.Uri
import com.company.restaurantpos.data.local.AppDatabase
import com.company.restaurantpos.data.local.entities.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for database backup and restore operations
 * Supports encrypted JSON export/import and cloud sync
 */
@Singleton
class BackupManager @Inject constructor(
    private val database: AppDatabase,
    private val context: Context
) {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    
    companion object {
        private const val BACKUP_VERSION = 1
        private const val ENCRYPTION_ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
        private const val BACKUP_FILE_EXTENSION = ".posbackup"
        private const val ENCRYPTION_KEY_LENGTH = 256
    }
    
    /**
     * Create a complete database backup
     * @param includeUsers Whether to include user data (passwords will be excluded for security)
     * @param encryptionKey Optional encryption key (if null, backup will be unencrypted)
     * @return BackupResult with success status and backup data
     */
    suspend fun createBackup(
        includeUsers: Boolean = true,
        encryptionKey: String? = null
    ): BackupResult = withContext(Dispatchers.IO) {
        try {
            val backupData = BackupData(
                version = BACKUP_VERSION,
                timestamp = System.currentTimeMillis(),
                users = if (includeUsers) getUsersForBackup() else emptyList(),
                customers = database.customerDao().getAllCustomers(),
                products = database.productDao().getAllProducts(),
                ingredients = database.ingredientDao().getAllIngredients(),
                recipes = database.recipeDao().getAllRecipes(),
                recipeIngredients = database.recipeIngredientDao().getAllRecipeIngredients(),
                orders = database.orderDao().getAllOrders(),
                orderItems = database.orderItemDao().getAllOrderItems(),
                payments = database.paymentDao().getAllPayments()
            )
            
            val jsonData = gson.toJson(backupData)
            
            val finalData = if (encryptionKey != null) {
                encryptData(jsonData, encryptionKey)
            } else {
                jsonData
            }
            
            BackupResult.Success(finalData, backupData.timestamp)
        } catch (e: Exception) {
            BackupResult.Error("Failed to create backup: ${e.message}")
        }
    }
    
    /**
     * Restore database from backup
     * @param backupData Backup data (encrypted or plain JSON)
     * @param encryptionKey Optional decryption key
     * @param replaceExisting Whether to replace existing data or merge
     * @return RestoreResult with success status
     */
    suspend fun restoreBackup(
        backupData: String,
        encryptionKey: String? = null,
        replaceExisting: Boolean = false
    ): RestoreResult = withContext(Dispatchers.IO) {
        try {
            val jsonData = if (encryptionKey != null) {
                decryptData(backupData, encryptionKey)
            } else {
                backupData
            }
            
            val backup = gson.fromJson(jsonData, BackupData::class.java)
            
            // Validate backup version
            if (backup.version > BACKUP_VERSION) {
                return@withContext RestoreResult.Error("Backup version ${backup.version} is not supported")
            }
            
            // Perform restore in transaction
            database.runInTransaction {
                if (replaceExisting) {
                    clearAllData()
                }
                
                restoreData(backup)
            }
            
            RestoreResult.Success(backup.timestamp)
        } catch (e: Exception) {
            RestoreResult.Error("Failed to restore backup: ${e.message}")
        }
    }
    
    /**
     * Export backup to file
     * @param uri File URI to write to
     * @param includeUsers Whether to include user data
     * @param encryptionKey Optional encryption key
     * @return ExportResult with success status
     */
    suspend fun exportToFile(
        uri: Uri,
        includeUsers: Boolean = true,
        encryptionKey: String? = null
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val backupResult = createBackup(includeUsers, encryptionKey)
            
            when (backupResult) {
                is BackupResult.Success -> {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(backupResult.data.toByteArray())
                    }
                    ExportResult.Success(uri, backupResult.timestamp)
                }
                is BackupResult.Error -> {
                    ExportResult.Error(backupResult.message)
                }
            }
        } catch (e: Exception) {
            ExportResult.Error("Failed to export backup: ${e.message}")
        }
    }
    
    /**
     * Import backup from file
     * @param uri File URI to read from
     * @param encryptionKey Optional decryption key
     * @param replaceExisting Whether to replace existing data
     * @return ImportResult with success status
     */
    suspend fun importFromFile(
        uri: Uri,
        encryptionKey: String? = null,
        replaceExisting: Boolean = false
    ): ImportResult = withContext(Dispatchers.IO) {
        try {
            val backupData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return@withContext ImportResult.Error("Failed to read backup file")
            
            val restoreResult = restoreBackup(backupData, encryptionKey, replaceExisting)
            
            when (restoreResult) {
                is RestoreResult.Success -> {
                    ImportResult.Success(uri, restoreResult.timestamp)
                }
                is RestoreResult.Error -> {
                    ImportResult.Error(restoreResult.message)
                }
            }
        } catch (e: Exception) {
            ImportResult.Error("Failed to import backup: ${e.message}")
        }
    }
    
    /**
     * Generate backup filename with timestamp
     */
    fun generateBackupFilename(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "restaurant_pos_backup_$timestamp$BACKUP_FILE_EXTENSION"
    }
    
    /**
     * Validate backup data
     * @param backupData Backup data to validate
     * @param encryptionKey Optional decryption key
     * @return ValidationResult with validation status
     */
    suspend fun validateBackup(
        backupData: String,
        encryptionKey: String? = null
    ): ValidationResult = withContext(Dispatchers.IO) {
        try {
            val jsonData = if (encryptionKey != null) {
                decryptData(backupData, encryptionKey)
            } else {
                backupData
            }
            
            val backup = gson.fromJson(jsonData, BackupData::class.java)
            
            ValidationResult.Success(
                version = backup.version,
                timestamp = backup.timestamp,
                userCount = backup.users.size,
                customerCount = backup.customers.size,
                productCount = backup.products.size,
                orderCount = backup.orders.size
            )
        } catch (e: Exception) {
            ValidationResult.Error("Invalid backup data: ${e.message}")
        }
    }
    
    /**
     * Get users for backup (excluding sensitive password data)
     */
    private suspend fun getUsersForBackup(): List<User> {
        return database.userDao().getAllUsers().map { user ->
            user.copy(passwordHash = "***EXCLUDED***")
        }
    }
    
    /**
     * Clear all data from database
     */
    private suspend fun clearAllData() {
        // Clear in reverse dependency order
        database.paymentDao().deleteAll()
        database.orderItemDao().deleteAll()
        database.orderDao().deleteAll()
        database.recipeIngredientDao().deleteAll()
        database.recipeDao().deleteAll()
        database.productDao().deleteAll()
        database.ingredientDao().deleteAll()
        database.customerDao().deleteAll()
        // Don't clear users to maintain login sessions
    }
    
    /**
     * Restore data from backup
     */
    private suspend fun restoreData(backup: BackupData) {
        // Restore in dependency order
        backup.customers.forEach { customer ->
            try {
                database.customerDao().insert(customer)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.ingredients.forEach { ingredient ->
            try {
                database.ingredientDao().insert(ingredient)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.products.forEach { product ->
            try {
                database.productDao().insert(product)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.recipes.forEach { recipe ->
            try {
                database.recipeDao().insert(recipe)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.recipeIngredients.forEach { recipeIngredient ->
            try {
                database.recipeIngredientDao().insert(recipeIngredient)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.orders.forEach { order ->
            try {
                database.orderDao().insert(order)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.orderItems.forEach { orderItem ->
            try {
                database.orderItemDao().insert(orderItem)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
        
        backup.payments.forEach { payment ->
            try {
                database.paymentDao().insert(payment)
            } catch (e: Exception) {
                // Skip duplicates or handle conflicts
            }
        }
    }
    
    /**
     * Encrypt data using AES
     */
    private fun encryptData(data: String, key: String): String {
        val secretKey = SecretKeySpec(key.toByteArray().take(32).toByteArray(), ENCRYPTION_ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }
    
    /**
     * Decrypt data using AES
     */
    private fun decryptData(encryptedData: String, key: String): String {
        val secretKey = SecretKeySpec(key.toByteArray().take(32).toByteArray(), ENCRYPTION_ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}

/**
 * Data class representing complete backup data
 */
data class BackupData(
    val version: Int,
    val timestamp: Long,
    val users: List<User>,
    val customers: List<Customer>,
    val products: List<Product>,
    val ingredients: List<Ingredient>,
    val recipes: List<Recipe>,
    val recipeIngredients: List<RecipeIngredient>,
    val orders: List<Order>,
    val orderItems: List<OrderItem>,
    val payments: List<Payment>
)

/**
 * Result classes for backup operations
 */
sealed class BackupResult {
    data class Success(val data: String, val timestamp: Long) : BackupResult()
    data class Error(val message: String) : BackupResult()
}

sealed class RestoreResult {
    data class Success(val timestamp: Long) : RestoreResult()
    data class Error(val message: String) : RestoreResult()
}

sealed class ExportResult {
    data class Success(val uri: Uri, val timestamp: Long) : ExportResult()
    data class Error(val message: String) : ExportResult()
}

sealed class ImportResult {
    data class Success(val uri: Uri, val timestamp: Long) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

sealed class ValidationResult {
    data class Success(
        val version: Int,
        val timestamp: Long,
        val userCount: Int,
        val customerCount: Int,
        val productCount: Int,
        val orderCount: Int
    ) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
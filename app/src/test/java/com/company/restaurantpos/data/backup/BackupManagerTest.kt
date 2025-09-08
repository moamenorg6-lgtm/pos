package com.company.restaurantpos.data.backup

import android.content.Context
import android.net.Uri
import com.company.restaurantpos.data.local.AppDatabase
import com.company.restaurantpos.data.local.daos.*
import com.company.restaurantpos.data.local.entities.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Unit tests for BackupManager
 */
@RunWith(RobolectricTestRunner::class)
class BackupManagerTest {
    
    private lateinit var backupManager: BackupManager
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockUserDao: UserDao
    private lateinit var mockCustomerDao: CustomerDao
    private lateinit var mockProductDao: ProductDao
    private lateinit var mockIngredientDao: IngredientDao
    private lateinit var mockRecipeDao: RecipeDao
    private lateinit var mockRecipeIngredientDao: RecipeIngredientDao
    private lateinit var mockOrderDao: OrderDao
    private lateinit var mockOrderItemDao: OrderItemDao
    private lateinit var mockPaymentDao: PaymentDao
    private lateinit var context: Context
    
    private val testUser = User(
        id = 1,
        username = "testuser",
        passwordHash = "hashedpassword",
        role = UserRole.ADMIN,
        isActive = true,
        createdAt = System.currentTimeMillis(),
        lastLoginAt = null
    )
    
    private val testCustomer = Customer(
        id = 1,
        name = "Test Customer",
        phone = "1234567890",
        email = "test@example.com",
        address = "Test Address",
        createdAt = System.currentTimeMillis()
    )
    
    private val testProduct = Product(
        id = 1,
        name = "Test Product",
        price = 10.0,
        category = "Test Category",
        description = "Test Description",
        isAvailable = true,
        createdAt = System.currentTimeMillis()
    )
    
    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        mockDatabase = mockk()
        mockUserDao = mockk()
        mockCustomerDao = mockk()
        mockProductDao = mockk()
        mockIngredientDao = mockk()
        mockRecipeDao = mockk()
        mockRecipeIngredientDao = mockk()
        mockOrderDao = mockk()
        mockOrderItemDao = mockk()
        mockPaymentDao = mockk()
        
        every { mockDatabase.userDao() } returns mockUserDao
        every { mockDatabase.customerDao() } returns mockCustomerDao
        every { mockDatabase.productDao() } returns mockProductDao
        every { mockDatabase.ingredientDao() } returns mockIngredientDao
        every { mockDatabase.recipeDao() } returns mockRecipeDao
        every { mockDatabase.recipeIngredientDao() } returns mockRecipeIngredientDao
        every { mockDatabase.orderDao() } returns mockOrderDao
        every { mockDatabase.orderItemDao() } returns mockOrderItemDao
        every { mockDatabase.paymentDao() } returns mockPaymentDao
        
        backupManager = BackupManager(mockDatabase, context)
    }
    
    @Test
    fun `createBackup should return success with valid data`() = runTest {
        // Given
        coEvery { mockUserDao.getAllUsers() } returns listOf(testUser)
        coEvery { mockCustomerDao.getAllCustomers() } returns listOf(testCustomer)
        coEvery { mockProductDao.getAllProducts() } returns listOf(testProduct)
        coEvery { mockIngredientDao.getAllIngredients() } returns emptyList()
        coEvery { mockRecipeDao.getAllRecipes() } returns emptyList()
        coEvery { mockRecipeIngredientDao.getAllRecipeIngredients() } returns emptyList()
        coEvery { mockOrderDao.getAllOrders() } returns emptyList()
        coEvery { mockOrderItemDao.getAllOrderItems() } returns emptyList()
        coEvery { mockPaymentDao.getAllPayments() } returns emptyList()
        
        // When
        val result = backupManager.createBackup(includeUsers = true, encryptionKey = null)
        
        // Then
        assertTrue(result is BackupResult.Success)
        val successResult = result as BackupResult.Success
        assertNotNull(successResult.data)
        assertTrue(successResult.data.isNotEmpty())
        assertTrue(successResult.timestamp > 0)
    }
    
    @Test
    fun `createBackup should exclude users when includeUsers is false`() = runTest {
        // Given
        coEvery { mockCustomerDao.getAllCustomers() } returns listOf(testCustomer)
        coEvery { mockProductDao.getAllProducts() } returns listOf(testProduct)
        coEvery { mockIngredientDao.getAllIngredients() } returns emptyList()
        coEvery { mockRecipeDao.getAllRecipes() } returns emptyList()
        coEvery { mockRecipeIngredientDao.getAllRecipeIngredients() } returns emptyList()
        coEvery { mockOrderDao.getAllOrders() } returns emptyList()
        coEvery { mockOrderItemDao.getAllOrderItems() } returns emptyList()
        coEvery { mockPaymentDao.getAllPayments() } returns emptyList()
        
        // When
        val result = backupManager.createBackup(includeUsers = false, encryptionKey = null)
        
        // Then
        assertTrue(result is BackupResult.Success)
        val successResult = result as BackupResult.Success
        assertNotNull(successResult.data)
        assertTrue(successResult.data.contains("\"users\":[]"))
        verify(exactly = 0) { mockUserDao.getAllUsers() }
    }
    
    @Test
    fun `createBackup should encrypt data when encryption key provided`() = runTest {
        // Given
        coEvery { mockUserDao.getAllUsers() } returns listOf(testUser)
        coEvery { mockCustomerDao.getAllCustomers() } returns emptyList()
        coEvery { mockProductDao.getAllProducts() } returns emptyList()
        coEvery { mockIngredientDao.getAllIngredients() } returns emptyList()
        coEvery { mockRecipeDao.getAllRecipes() } returns emptyList()
        coEvery { mockRecipeIngredientDao.getAllRecipeIngredients() } returns emptyList()
        coEvery { mockOrderDao.getAllOrders() } returns emptyList()
        coEvery { mockOrderItemDao.getAllOrderItems() } returns emptyList()
        coEvery { mockPaymentDao.getAllPayments() } returns emptyList()
        
        val encryptionKey = "testencryptionkey123456789012"
        
        // When
        val result = backupManager.createBackup(includeUsers = true, encryptionKey = encryptionKey)
        
        // Then
        assertTrue(result is BackupResult.Success)
        val successResult = result as BackupResult.Success
        assertNotNull(successResult.data)
        // Encrypted data should not contain readable JSON
        assertFalse(successResult.data.contains("\"users\""))
        assertFalse(successResult.data.contains("testuser"))
    }
    
    @Test
    fun `createBackup should exclude password hashes from user data`() = runTest {
        // Given
        coEvery { mockUserDao.getAllUsers() } returns listOf(testUser)
        coEvery { mockCustomerDao.getAllCustomers() } returns emptyList()
        coEvery { mockProductDao.getAllProducts() } returns emptyList()
        coEvery { mockIngredientDao.getAllIngredients() } returns emptyList()
        coEvery { mockRecipeDao.getAllRecipes() } returns emptyList()
        coEvery { mockRecipeIngredientDao.getAllRecipeIngredients() } returns emptyList()
        coEvery { mockOrderDao.getAllOrders() } returns emptyList()
        coEvery { mockOrderItemDao.getAllOrderItems() } returns emptyList()
        coEvery { mockPaymentDao.getAllPayments() } returns emptyList()
        
        // When
        val result = backupManager.createBackup(includeUsers = true, encryptionKey = null)
        
        // Then
        assertTrue(result is BackupResult.Success)
        val successResult = result as BackupResult.Success
        assertTrue(successResult.data.contains("***EXCLUDED***"))
        assertFalse(successResult.data.contains("hashedpassword"))
    }
    
    @Test
    fun `restoreBackup should return success with valid data`() = runTest {
        // Given
        val backupData = """
            {
                "version": 1,
                "timestamp": ${System.currentTimeMillis()},
                "users": [],
                "customers": [${createCustomerJson()}],
                "products": [${createProductJson()}],
                "ingredients": [],
                "recipes": [],
                "recipeIngredients": [],
                "orders": [],
                "orderItems": [],
                "payments": []
            }
        """.trimIndent()
        
        coEvery { mockDatabase.runInTransaction(any<suspend () -> Unit>()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        coEvery { mockCustomerDao.insert(any()) } just Runs
        coEvery { mockProductDao.insert(any()) } just Runs
        
        // When
        val result = backupManager.restoreBackup(backupData, encryptionKey = null, replaceExisting = false)
        
        // Then
        assertTrue(result is RestoreResult.Success)
        coVerify { mockCustomerDao.insert(any()) }
        coVerify { mockProductDao.insert(any()) }
    }
    
    @Test
    fun `restoreBackup should return error for invalid JSON`() = runTest {
        // Given
        val invalidBackupData = "invalid json data"
        
        // When
        val result = backupManager.restoreBackup(invalidBackupData, encryptionKey = null, replaceExisting = false)
        
        // Then
        assertTrue(result is RestoreResult.Error)
        assertTrue((result as RestoreResult.Error).message.contains("Failed to restore backup"))
    }
    
    @Test
    fun `restoreBackup should return error for unsupported version`() = runTest {
        // Given
        val futureVersionBackup = """
            {
                "version": 999,
                "timestamp": ${System.currentTimeMillis()},
                "users": [],
                "customers": [],
                "products": [],
                "ingredients": [],
                "recipes": [],
                "recipeIngredients": [],
                "orders": [],
                "orderItems": [],
                "payments": []
            }
        """.trimIndent()
        
        // When
        val result = backupManager.restoreBackup(futureVersionBackup, encryptionKey = null, replaceExisting = false)
        
        // Then
        assertTrue(result is RestoreResult.Error)
        assertTrue((result as RestoreResult.Error).message.contains("version 999 is not supported"))
    }
    
    @Test
    fun `validateBackup should return success for valid backup`() = runTest {
        // Given
        val validBackupData = """
            {
                "version": 1,
                "timestamp": ${System.currentTimeMillis()},
                "users": [${createUserJson()}],
                "customers": [${createCustomerJson()}],
                "products": [${createProductJson()}],
                "ingredients": [],
                "recipes": [],
                "recipeIngredients": [],
                "orders": [],
                "orderItems": [],
                "payments": []
            }
        """.trimIndent()
        
        // When
        val result = backupManager.validateBackup(validBackupData, encryptionKey = null)
        
        // Then
        assertTrue(result is ValidationResult.Success)
        val successResult = result as ValidationResult.Success
        assertEquals(1, successResult.version)
        assertEquals(1, successResult.userCount)
        assertEquals(1, successResult.customerCount)
        assertEquals(1, successResult.productCount)
        assertEquals(0, successResult.orderCount)
    }
    
    @Test
    fun `validateBackup should return error for invalid backup`() = runTest {
        // Given
        val invalidBackupData = "invalid json"
        
        // When
        val result = backupManager.validateBackup(invalidBackupData, encryptionKey = null)
        
        // Then
        assertTrue(result is ValidationResult.Error)
        assertTrue((result as ValidationResult.Error).message.contains("Invalid backup data"))
    }
    
    @Test
    fun `generateBackupFilename should return valid filename`() {
        // When
        val filename = backupManager.generateBackupFilename()
        
        // Then
        assertNotNull(filename)
        assertTrue(filename.startsWith("restaurant_pos_backup_"))
        assertTrue(filename.endsWith(".posbackup"))
        assertTrue(filename.contains("_")) // Should contain date/time separators
    }
    
    private fun createUserJson(): String {
        return """
            {
                "id": 1,
                "username": "testuser",
                "passwordHash": "***EXCLUDED***",
                "role": "ADMIN",
                "isActive": true,
                "createdAt": ${System.currentTimeMillis()},
                "lastLoginAt": null
            }
        """.trimIndent()
    }
    
    private fun createCustomerJson(): String {
        return """
            {
                "id": 1,
                "name": "Test Customer",
                "phone": "1234567890",
                "email": "test@example.com",
                "address": "Test Address",
                "createdAt": ${System.currentTimeMillis()}
            }
        """.trimIndent()
    }
    
    private fun createProductJson(): String {
        return """
            {
                "id": 1,
                "name": "Test Product",
                "price": 10.0,
                "category": "Test Category",
                "description": "Test Description",
                "isAvailable": true,
                "createdAt": ${System.currentTimeMillis()}
            }
        """.trimIndent()
    }
}
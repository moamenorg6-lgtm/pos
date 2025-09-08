package com.company.restaurantpos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.company.restaurantpos.data.local.daos.UserDao
import com.company.restaurantpos.data.local.entities.User
import com.company.restaurantpos.data.local.entities.UserRole
import com.company.restaurantpos.utils.PasswordUtils
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit tests for AuthRepository
 */
@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var mockUserDao: UserDao
    private lateinit var context: Context
    
    private val testUser = User(
        id = 1,
        username = "testuser",
        passwordHash = PasswordUtils.hashPassword("password123"),
        role = UserRole.ADMIN,
        isActive = true,
        createdAt = System.currentTimeMillis(),
        lastLoginAt = null
    )
    
    @Before
    fun setup() {
        mockUserDao = mockk()
        context = RuntimeEnvironment.getApplication()
        authRepository = AuthRepository(mockUserDao, context)
    }
    
    @Test
    fun `login should return success for valid credentials`() = runTest {
        // Given
        coEvery { mockUserDao.getByUsername("testuser") } returns testUser
        coEvery { mockUserDao.updateLastLogin(any(), any()) } just Runs
        
        // When
        val result = authRepository.login("testuser", "password123")
        
        // Then
        assertTrue(result is LoginResult.Success)
        assertEquals(testUser, (result as LoginResult.Success).user)
        coVerify { mockUserDao.updateLastLogin(testUser.id, any()) }
    }
    
    @Test
    fun `login should return error for invalid username`() = runTest {
        // Given
        coEvery { mockUserDao.getByUsername("invaliduser") } returns null
        
        // When
        val result = authRepository.login("invaliduser", "password123")
        
        // Then
        assertTrue(result is LoginResult.Error)
        assertEquals("Invalid username or password", (result as LoginResult.Error).message)
    }
    
    @Test
    fun `login should return error for invalid password`() = runTest {
        // Given
        coEvery { mockUserDao.getByUsername("testuser") } returns testUser
        
        // When
        val result = authRepository.login("testuser", "wrongpassword")
        
        // Then
        assertTrue(result is LoginResult.Error)
        assertEquals("Invalid username or password", (result as LoginResult.Error).message)
    }
    
    @Test
    fun `login should return error for inactive user`() = runTest {
        // Given
        val inactiveUser = testUser.copy(isActive = false)
        coEvery { mockUserDao.getByUsername("testuser") } returns inactiveUser
        
        // When
        val result = authRepository.login("testuser", "password123")
        
        // Then
        assertTrue(result is LoginResult.Error)
        assertTrue((result as LoginResult.Error).message.contains("deactivated"))
    }
    
    @Test
    fun `login should return error for blank credentials`() = runTest {
        // When
        val result1 = authRepository.login("", "password123")
        val result2 = authRepository.login("testuser", "")
        val result3 = authRepository.login("", "")
        
        // Then
        assertTrue(result1 is LoginResult.Error)
        assertTrue(result2 is LoginResult.Error)
        assertTrue(result3 is LoginResult.Error)
        
        assertEquals("Username and password are required", (result1 as LoginResult.Error).message)
        assertEquals("Username and password are required", (result2 as LoginResult.Error).message)
        assertEquals("Username and password are required", (result3 as LoginResult.Error).message)
    }
    
    @Test
    fun `createUser should return success for admin user`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser // Current user is admin
        coEvery { mockUserDao.usernameExists("newuser") } returns false
        coEvery { mockUserDao.insert(any()) } returns 2L
        
        // When
        val result = authRepository.createUser("newuser", "password123", UserRole.CASHIER)
        
        // Then
        assertTrue(result is CreateUserResult.Success)
        assertEquals(2L, (result as CreateUserResult.Success).userId)
        coVerify { mockUserDao.insert(any()) }
    }
    
    @Test
    fun `createUser should return error for non-admin user`() = runTest {
        // Given
        val cashierUser = testUser.copy(role = UserRole.CASHIER)
        coEvery { mockUserDao.getById(1) } returns cashierUser
        
        // When
        val result = authRepository.createUser("newuser", "password123", UserRole.CASHIER)
        
        // Then
        assertTrue(result is CreateUserResult.Error)
        assertTrue((result as CreateUserResult.Error).message.contains("administrator"))
    }
    
    @Test
    fun `createUser should return error for existing username`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        coEvery { mockUserDao.usernameExists("existinguser") } returns true
        
        // When
        val result = authRepository.createUser("existinguser", "password123", UserRole.CASHIER)
        
        // Then
        assertTrue(result is CreateUserResult.Error)
        assertTrue((result as CreateUserResult.Error).message.contains("already exists"))
    }
    
    @Test
    fun `createUser should return error for weak password`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        coEvery { mockUserDao.usernameExists("newuser") } returns false
        
        // When
        val result = authRepository.createUser("newuser", "weak", UserRole.CASHIER)
        
        // Then
        assertTrue(result is CreateUserResult.Error)
        assertTrue((result as CreateUserResult.Error).message.contains("6 characters"))
    }
    
    @Test
    fun `createUser should return error for blank username`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        
        // When
        val result = authRepository.createUser("", "password123", UserRole.CASHIER)
        
        // Then
        assertTrue(result is CreateUserResult.Error)
        assertEquals("Username is required", (result as CreateUserResult.Error).message)
    }
    
    @Test
    fun `changePassword should return success for correct current password`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        coEvery { mockUserDao.updatePassword(1, any()) } returns 1
        
        // When
        val result = authRepository.changePassword("password123", "newpassword123")
        
        // Then
        assertTrue(result is ChangePasswordResult.Success)
        coVerify { mockUserDao.updatePassword(1, any()) }
    }
    
    @Test
    fun `changePassword should return error for incorrect current password`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        
        // When
        val result = authRepository.changePassword("wrongpassword", "newpassword123")
        
        // Then
        assertTrue(result is ChangePasswordResult.Error)
        assertEquals("Current password is incorrect", (result as ChangePasswordResult.Error).message)
    }
    
    @Test
    fun `changePassword should return error for weak new password`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        
        // When
        val result = authRepository.changePassword("password123", "weak")
        
        // Then
        assertTrue(result is ChangePasswordResult.Error)
        assertTrue((result as ChangePasswordResult.Error).message.contains("6 characters"))
    }
    
    @Test
    fun `hasPermission should return true for user with permission`() = runTest {
        // Given
        coEvery { mockUserDao.getById(1) } returns testUser
        
        // When
        val result = authRepository.hasPermission(com.company.restaurantpos.data.local.entities.Permission.VIEW_REPORTS)
        
        // Then
        assertTrue(result) // Admin has all permissions
    }
    
    @Test
    fun `hasPermission should return false for user without permission`() = runTest {
        // Given
        val cashierUser = testUser.copy(role = UserRole.CASHIER)
        coEvery { mockUserDao.getById(1) } returns cashierUser
        
        // When
        val result = authRepository.hasPermission(com.company.restaurantpos.data.local.entities.Permission.VIEW_REPORTS)
        
        // Then
        assertFalse(result) // Cashier doesn't have VIEW_REPORTS permission
    }
    
    @Test
    fun `hasPermission should return false when no user is logged in`() = runTest {
        // Given
        coEvery { mockUserDao.getById(any()) } returns null
        
        // When
        val result = authRepository.hasPermission(com.company.restaurantpos.data.local.entities.Permission.VIEW_REPORTS)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `initializeDefaultUser should create admin user when none exists`() = runTest {
        // Given
        coEvery { mockUserDao.getCountByRole(UserRole.ADMIN) } returns 0
        coEvery { mockUserDao.insert(any()) } returns 1L
        
        // When
        authRepository.initializeDefaultUser()
        
        // Then
        coVerify { 
            mockUserDao.insert(match { user ->
                user.username == "admin" && user.role == UserRole.ADMIN
            })
        }
    }
    
    @Test
    fun `initializeDefaultUser should not create admin user when one exists`() = runTest {
        // Given
        coEvery { mockUserDao.getCountByRole(UserRole.ADMIN) } returns 1
        
        // When
        authRepository.initializeDefaultUser()
        
        // Then
        coVerify(exactly = 0) { mockUserDao.insert(any()) }
    }
}
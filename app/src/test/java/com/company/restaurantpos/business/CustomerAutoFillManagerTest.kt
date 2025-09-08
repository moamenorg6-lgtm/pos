package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.daos.CustomerDao
import com.company.restaurantpos.data.local.entities.Customer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CustomerAutoFillManagerTest {
    
    private lateinit var customerDao: CustomerDao
    private lateinit var customerAutoFillManager: CustomerAutoFillManager
    
    @Before
    fun setup() {
        customerDao = mockk()
        customerAutoFillManager = CustomerAutoFillManager(customerDao)
    }
    
    @Test
    fun `searchCustomerByPhone returns customer when found`() = runTest {
        // Given
        val phone = "1234567890"
        val expectedCustomer = Customer(
            id = 1,
            name = "John Doe",
            phone = phone,
            address = "123 Main St"
        )
        coEvery { customerDao.searchByPhone(phone) } returns listOf(expectedCustomer)
        
        // When
        val result = customerAutoFillManager.searchCustomerByPhone(phone)
        
        // Then
        assertEquals(expectedCustomer, result)
        coVerify { customerDao.searchByPhone(phone) }
    }
    
    @Test
    fun `searchCustomerByPhone returns null when not found`() = runTest {
        // Given
        val phone = "1234567890"
        coEvery { customerDao.searchByPhone(phone) } returns emptyList()
        
        // When
        val result = customerAutoFillManager.searchCustomerByPhone(phone)
        
        // Then
        assertNull(result)
        coVerify { customerDao.searchByPhone(phone) }
    }
    
    @Test
    fun `searchCustomerByPhone returns null for short phone number`() = runTest {
        // Given
        val phone = "12"
        
        // When
        val result = customerAutoFillManager.searchCustomerByPhone(phone)
        
        // Then
        assertNull(result)
        coVerify(exactly = 0) { customerDao.searchByPhone(any()) }
    }
    
    @Test
    fun `createCustomer returns customer when successful`() = runTest {
        // Given
        val name = "John Doe"
        val phone = "1234567890"
        val address = "123 Main St"
        val customerId = 1L
        
        coEvery { customerDao.insert(any()) } returns customerId
        
        // When
        val result = customerAutoFillManager.createCustomer(name, phone, address)
        
        // Then
        assertNotNull(result)
        assertEquals(customerId, result?.id)
        assertEquals(name, result?.name)
        assertEquals(phone, result?.phone)
        assertEquals(address, result?.address)
        coVerify { customerDao.insert(any()) }
    }
    
    @Test
    fun `validateCustomerData returns valid for correct data`() {
        // Given
        val name = "John Doe"
        val phone = "1234567890"
        
        // When
        val result = customerAutoFillManager.validateCustomerData(name, phone)
        
        // Then
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }
    
    @Test
    fun `validateCustomerData returns invalid for blank name`() {
        // Given
        val name = ""
        val phone = "1234567890"
        
        // When
        val result = customerAutoFillManager.validateCustomerData(name, phone)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.errors.contains("Name is required"))
    }
    
    @Test
    fun `validateCustomerData returns invalid for blank phone`() {
        // Given
        val name = "John Doe"
        val phone = ""
        
        // When
        val result = customerAutoFillManager.validateCustomerData(name, phone)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.errors.contains("Phone number is required"))
    }
    
    @Test
    fun `validateCustomerData returns invalid for short phone`() {
        // Given
        val name = "John Doe"
        val phone = "123"
        
        // When
        val result = customerAutoFillManager.validateCustomerData(name, phone)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.errors.contains("Phone number must be at least 8 digits"))
    }
}
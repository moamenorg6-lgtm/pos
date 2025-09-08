package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.daos.CustomerDao
import com.company.restaurantpos.data.local.entities.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages customer auto-fill functionality
 */
@Singleton
class CustomerAutoFillManager @Inject constructor(
    private val customerDao: CustomerDao
) {
    
    /**
     * Searches for a customer by phone number
     * Returns the customer if found, null otherwise
     */
    suspend fun searchCustomerByPhone(phone: String): Customer? = withContext(Dispatchers.IO) {
        if (phone.length < 3) return@withContext null
        
        try {
            customerDao.searchByPhone(phone).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Creates a new customer
     */
    suspend fun createCustomer(
        name: String,
        phone: String,
        address: String = ""
    ): Customer? = withContext(Dispatchers.IO) {
        try {
            val customer = Customer(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim()
            )
            
            val customerId = customerDao.insert(customer)
            customer.copy(id = customerId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Updates an existing customer
     */
    suspend fun updateCustomer(customer: Customer): Boolean = withContext(Dispatchers.IO) {
        try {
            customerDao.update(customer)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Validates customer data
     */
    fun validateCustomerData(name: String, phone: String): CustomerValidationResult {
        val errors = mutableListOf<String>()
        
        if (name.isBlank()) {
            errors.add("Name is required")
        }
        
        if (phone.isBlank()) {
            errors.add("Phone number is required")
        } else if (phone.length < 8) {
            errors.add("Phone number must be at least 8 digits")
        } else if (!phone.all { it.isDigit() || it in listOf('+', '-', ' ', '(', ')') }) {
            errors.add("Phone number contains invalid characters")
        }
        
        return CustomerValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}

/**
 * Result of customer data validation
 */
data class CustomerValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)
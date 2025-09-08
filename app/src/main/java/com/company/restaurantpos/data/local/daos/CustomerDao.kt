package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Customer

/**
 * Data Access Object for Customer entity
 * Provides methods for customer-related database operations
 */
@Dao
interface CustomerDao {
    
    /**
     * Get all customers
     */
    @Query("SELECT * FROM customers ORDER BY name ASC")
    suspend fun getAllCustomers(): List<Customer>
    
    /**
     * Get customer by exact phone number
     * @param phone Exact phone number to search for
     * @return Customer if found, null otherwise
     */
    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun getByPhoneExact(phone: String): Customer?
    
    /**
     * Search customers by partial phone number
     * @param phonePartial Partial phone number to search for
     * @return List of customers with matching phone numbers
     */
    @Query("SELECT * FROM customers WHERE phone LIKE '%' || :phonePartial || '%' ORDER BY name ASC")
    suspend fun searchByPhonePartial(phonePartial: String): List<Customer>
    
    /**
     * Search customers by name
     * @param nameQuery Name query to search for
     * @return List of customers with matching names
     */
    @Query("SELECT * FROM customers WHERE name LIKE '%' || :nameQuery || '%' ORDER BY name ASC")
    suspend fun searchByName(nameQuery: String): List<Customer>
    
    /**
     * Get customer by ID
     * @param customerId Customer ID
     * @return Customer if found, null otherwise
     */
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getById(customerId: Int): Customer?
    
    /**
     * Insert a new customer
     * @param customer Customer to insert
     * @return Row ID of inserted customer
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: Customer): Long
    
    /**
     * Update existing customer
     * @param customer Customer to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(customer: Customer): Int
    
    /**
     * Delete customer
     * @param customer Customer to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(customer: Customer): Int
    
    /**
     * Delete customer by ID
     * @param customerId Customer ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun deleteById(customerId: Int): Int
}
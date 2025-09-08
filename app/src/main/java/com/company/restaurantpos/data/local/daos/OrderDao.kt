package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Order

/**
 * Data Access Object for Order entity
 * Provides methods for order-related database operations
 */
@Dao
interface OrderDao {
    
    /**
     * Get all orders
     */
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAllOrders(): List<Order>
    
    /**
     * Get order by ID
     * @param orderId Order ID
     * @return Order if found, null otherwise
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getById(orderId: Int): Order?
    
    /**
     * Get order by order number
     * @param orderNo Order number
     * @return Order if found, null otherwise
     */
    @Query("SELECT * FROM orders WHERE orderNo = :orderNo")
    suspend fun getByOrderNo(orderNo: String): Order?
    
    /**
     * Get orders by status
     * @param statusList List of statuses to filter by
     * @return List of orders with matching statuses
     */
    @Query("SELECT * FROM orders WHERE status IN (:statusList) ORDER BY createdAt ASC")
    suspend fun getOrdersByStatus(statusList: List<String>): List<Order>
    
    /**
     * Get orders by customer ID
     * @param customerId Customer ID
     * @return List of orders for the customer
     */
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    suspend fun getByCustomerId(customerId: Int): List<Order>
    
    /**
     * Get orders by type
     * @param type Order type (dine_in, takeaway, delivery)
     * @return List of orders of the specified type
     */
    @Query("SELECT * FROM orders WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getByType(type: String): List<Order>
    
    /**
     * Get orders within date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of orders within the date range
     */
    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    suspend fun getByDateRange(startTime: Long, endTime: Long): List<Order>
    
    /**
     * Get today's orders
     * @param startOfDay Start of day timestamp
     * @param endOfDay End of day timestamp
     * @return List of today's orders
     */
    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY createdAt DESC")
    suspend fun getTodaysOrders(startOfDay: Long, endOfDay: Long): List<Order>
    
    /**
     * Get pending orders (new, preparing, ready)
     * @return List of pending orders
     */
    @Query("SELECT * FROM orders WHERE status IN ('new', 'preparing', 'ready') ORDER BY createdAt ASC")
    suspend fun getPendingOrders(): List<Order>
    
    /**
     * Update order status
     * @param orderId Order ID
     * @param newStatus New status
     * @return Number of rows updated
     */
    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    suspend fun updateStatus(orderId: Int, newStatus: String): Int
    
    /**
     * Update order total
     * @param orderId Order ID
     * @param newTotal New total amount
     * @return Number of rows updated
     */
    @Query("UPDATE orders SET total = :newTotal WHERE id = :orderId")
    suspend fun updateTotal(orderId: Int, newTotal: Double): Int
    
    /**
     * Insert a new order
     * @param order Order to insert
     * @return Row ID of inserted order
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(order: Order): Long
    
    /**
     * Update existing order
     * @param order Order to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(order: Order): Int
    
    /**
     * Delete order
     * @param order Order to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(order: Order): Int
    
    /**
     * Delete order by ID
     * @param orderId Order ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteById(orderId: Int): Int
}
package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Payment

/**
 * Data Access Object for Payment entity
 * Provides methods for payment-related database operations
 */
@Dao
interface PaymentDao {
    
    /**
     * Get all payments
     */
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    suspend fun getAllPayments(): List<Payment>
    
    /**
     * Get payment by ID
     * @param paymentId Payment ID
     * @return Payment if found, null otherwise
     */
    @Query("SELECT * FROM payments WHERE id = :paymentId")
    suspend fun getById(paymentId: Int): Payment?
    
    /**
     * Get all payments for a specific order
     * @param orderId Order ID
     * @return List of payments for the order
     */
    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt ASC")
    suspend fun getByOrderId(orderId: Int): List<Payment>
    
    /**
     * Get payments by method within date range
     * @param method Payment method
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of payments with the specified method
     */
    @Query("SELECT * FROM payments WHERE method = :method AND createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    suspend fun getByMethodAndDateRange(method: String, startTime: Long, endTime: Long): List<Payment>
    
    /**
     * Get payments within date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of payments within the date range
     */
    @Query("SELECT * FROM payments WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    suspend fun getByDateRange(startTime: Long, endTime: Long): List<Payment>
    
    /**
     * Get total amount paid for an order
     * @param orderId Order ID
     * @return Total amount paid
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE orderId = :orderId")
    suspend fun getTotalPaidForOrder(orderId: Int): Double
    
    /**
     * Get total payments by method within date range
     * @param method Payment method
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return Total amount for the payment method
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE method = :method AND createdAt BETWEEN :startTime AND :endTime")
    suspend fun getTotalByMethodAndDateRange(method: String, startTime: Long, endTime: Long): Double
    
    /**
     * Get total payments within date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return Total payment amount
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE createdAt BETWEEN :startTime AND :endTime")
    suspend fun getTotalByDateRange(startTime: Long, endTime: Long): Double
    
    /**
     * Get payment method statistics within date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of payment methods with totals
     */
    @Query("""
        SELECT method, SUM(amount) as total, COUNT(*) as count
        FROM payments 
        WHERE createdAt BETWEEN :startTime AND :endTime
        GROUP BY method
        ORDER BY total DESC
    """)
    suspend fun getPaymentMethodStats(startTime: Long, endTime: Long): List<PaymentMethodStats>
    
    /**
     * Get today's payments
     * @param startOfDay Start of day timestamp
     * @param endOfDay End of day timestamp
     * @return List of today's payments
     */
    @Query("SELECT * FROM payments WHERE createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY createdAt DESC")
    suspend fun getTodaysPayments(startOfDay: Long, endOfDay: Long): List<Payment>
    
    /**
     * Insert a new payment
     * @param payment Payment to insert
     * @return Row ID of inserted payment
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payment: Payment): Long
    
    /**
     * Insert multiple payments
     * @param payments List of payments to insert
     * @return List of row IDs of inserted payments
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(payments: List<Payment>): List<Long>
    
    /**
     * Update existing payment
     * @param payment Payment to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(payment: Payment): Int
    
    /**
     * Delete payment
     * @param payment Payment to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(payment: Payment): Int
    
    /**
     * Delete payment by ID
     * @param paymentId Payment ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deleteById(paymentId: Int): Int
    
    /**
     * Delete all payments for an order
     * @param orderId Order ID
     * @return Number of rows deleted
     */
    @Query("DELETE FROM payments WHERE orderId = :orderId")
    suspend fun deleteByOrderId(orderId: Int): Int
}

/**
 * Data class for payment method statistics
 */
data class PaymentMethodStats(
    val method: String,
    val total: Double,
    val count: Int
)
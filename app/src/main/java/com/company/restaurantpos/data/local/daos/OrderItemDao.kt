package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.OrderItem

/**
 * Data Access Object for OrderItem entity
 * Provides methods for order item-related database operations
 */
@Dao
interface OrderItemDao {
    
    /**
     * Get all order items for a specific order
     * @param orderId Order ID
     * @return List of order items
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getByOrderId(orderId: Int): List<OrderItem>
    
    /**
     * Get order items with product details for a specific order
     * @param orderId Order ID
     * @return List of order items with product information
     */
    @Query("""
        SELECT oi.*, p.nameEn, p.nameAr, p.category
        FROM order_items oi
        INNER JOIN products p ON oi.productId = p.id
        WHERE oi.orderId = :orderId
        ORDER BY oi.id ASC
    """)
    suspend fun getOrderItemsWithProductDetails(orderId: Int): List<OrderItemWithProductDetails>
    
    /**
     * Get order item by ID
     * @param orderItemId Order item ID
     * @return Order item if found, null otherwise
     */
    @Query("SELECT * FROM order_items WHERE id = :orderItemId")
    suspend fun getById(orderItemId: Int): OrderItem?
    
    /**
     * Get all order items for a specific product
     * @param productId Product ID
     * @return List of order items
     */
    @Query("SELECT * FROM order_items WHERE productId = :productId")
    suspend fun getByProductId(productId: Int): List<OrderItem>
    
    /**
     * Get total quantity sold for a product within date range
     * @param productId Product ID
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return Total quantity sold
     */
    @Query("""
        SELECT COALESCE(SUM(oi.qty), 0) 
        FROM order_items oi
        INNER JOIN orders o ON oi.orderId = o.id
        WHERE oi.productId = :productId 
        AND o.createdAt BETWEEN :startTime AND :endTime
        AND o.status NOT IN ('cancelled')
    """)
    suspend fun getTotalQuantitySold(productId: Int, startTime: Long, endTime: Long): Double
    
    /**
     * Get best selling products within date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @param limit Number of products to return
     * @return List of product IDs with total quantities sold
     */
    @Query("""
        SELECT oi.productId, SUM(oi.qty) as totalQty
        FROM order_items oi
        INNER JOIN orders o ON oi.orderId = o.id
        WHERE o.createdAt BETWEEN :startTime AND :endTime
        AND o.status NOT IN ('cancelled')
        GROUP BY oi.productId
        ORDER BY totalQty DESC
        LIMIT :limit
    """)
    suspend fun getBestSellingProducts(startTime: Long, endTime: Long, limit: Int): List<ProductSales>
    
    /**
     * Get top selling products with product details for reports
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @param limit Number of products to return
     * @return List of top selling products with details
     */
    @Query("""
        SELECT 
            p.id as productId,
            p.nameEn,
            p.nameAr,
            p.category,
            SUM(oi.qty) as totalQty,
            COALESCE(SUM(oi.qty * oi.price), 0) as totalRevenue,
            COALESCE(AVG(oi.price), 0) as averagePrice
        FROM order_items oi
        INNER JOIN orders o ON oi.orderId = o.id
        INNER JOIN products p ON oi.productId = p.id
        WHERE o.createdAt BETWEEN :startTime AND :endTime
        AND o.status NOT IN ('cancelled')
        GROUP BY p.id, p.nameEn, p.nameAr, p.category
        ORDER BY totalQty DESC
        LIMIT :limit
    """)
    suspend fun getTopSellingProductsWithDetails(startTime: Long, endTime: Long, limit: Int): List<TopSellingProduct>
    
    /**
     * Calculate total revenue for a date range
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return Total revenue
     */
    @Query("""
        SELECT COALESCE(SUM(oi.qty * oi.price), 0)
        FROM order_items oi
        INNER JOIN orders o ON oi.orderId = o.id
        WHERE o.createdAt BETWEEN :startTime AND :endTime
        AND o.status NOT IN ('cancelled')
    """)
    suspend fun getTotalRevenue(startTime: Long, endTime: Long): Double
    
    /**
     * Insert a new order item
     * @param orderItem Order item to insert
     * @return Row ID of inserted order item
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(orderItem: OrderItem): Long
    
    /**
     * Insert multiple order items
     * @param orderItems List of order items to insert
     * @return List of row IDs of inserted order items
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(orderItems: List<OrderItem>): List<Long>
    
    /**
     * Update existing order item
     * @param orderItem Order item to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(orderItem: OrderItem): Int
    
    /**
     * Delete order item
     * @param orderItem Order item to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(orderItem: OrderItem): Int
    
    /**
     * Delete order item by ID
     * @param orderItemId Order item ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM order_items WHERE id = :orderItemId")
    suspend fun deleteById(orderItemId: Int): Int
    
    /**
     * Delete all order items for an order
     * @param orderId Order ID
     * @return Number of rows deleted
     */
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteByOrderId(orderId: Int): Int
}

/**
 * Data class for order item with product details
 */
data class OrderItemWithProductDetails(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val qty: Double,
    val price: Double,
    val notes: String?,
    val nameEn: String,
    val nameAr: String,
    val category: String
)

/**
 * Data class for product sales statistics
 */
data class ProductSales(
    val productId: Int,
    val totalQty: Double
)

/**
 * Data class for top selling products with details
 */
data class TopSellingProduct(
    val productId: Int,
    val nameEn: String,
    val nameAr: String,
    val category: String,
    val totalQty: Double,
    val totalRevenue: Double,
    val averagePrice: Double
)
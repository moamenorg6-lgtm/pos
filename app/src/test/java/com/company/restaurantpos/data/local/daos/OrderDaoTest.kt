package com.company.restaurantpos.data.local.daos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.company.restaurantpos.data.local.RestaurantDatabase
import com.company.restaurantpos.data.local.entities.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderDaoTest {
    
    private lateinit var database: RestaurantDatabase
    private lateinit var orderDao: OrderDao
    private lateinit var customerDao: CustomerDao
    private lateinit var productDao: ProductDao
    private lateinit var orderItemDao: OrderItemDao
    private lateinit var paymentDao: PaymentDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RestaurantDatabase::class.java
        ).allowMainThreadQueries().build()
        
        orderDao = database.orderDao()
        customerDao = database.customerDao()
        productDao = database.productDao()
        orderItemDao = database.orderItemDao()
        paymentDao = database.paymentDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun getDailySalesSummary_returnsCorrectSummary() = runTest {
        // Insert test data
        val customerId = customerDao.insert(createTestCustomer())
        val productId = productDao.insert(createTestProduct())
        
        val order1Id = orderDao.insert(createTestOrder(1, customerId.toInt(), 100.0, 10.0, 110.0))
        val order2Id = orderDao.insert(createTestOrder(2, customerId.toInt(), 50.0, 5.0, 55.0))
        
        orderItemDao.insert(createTestOrderItem(order1Id.toInt(), productId.toInt(), 2.0, 50.0))
        orderItemDao.insert(createTestOrderItem(order2Id.toInt(), productId.toInt(), 1.0, 50.0))
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000) // 24 hours ago
        val endTime = currentTime + (60 * 60 * 1000) // 1 hour from now
        
        // Test the query
        val summary = orderDao.getDailySalesSummary(startTime, endTime)
        
        assertEquals("Should have 2 orders", 2, summary.totalOrders)
        assertEquals("Total revenue should be 165.0", 165.0, summary.totalRevenue, 0.01)
        assertEquals("Average order value should be 82.5", 82.5, summary.averageOrderValue, 0.01)
    }
    
    @Test
    fun getDailySalesSummary_withNoOrders_returnsZeroSummary() = runTest {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val summary = orderDao.getDailySalesSummary(startTime, endTime)
        
        assertEquals("Should have 0 orders", 0, summary.totalOrders)
        assertEquals("Total revenue should be 0.0", 0.0, summary.totalRevenue, 0.01)
        assertEquals("Average order value should be 0.0", 0.0, summary.averageOrderValue, 0.01)
    }
    
    @Test
    fun getOrderStatusCounts_returnsCorrectCounts() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        
        // Insert orders with different statuses
        orderDao.insert(createTestOrderWithStatus(1, customerId.toInt(), "pending"))
        orderDao.insert(createTestOrderWithStatus(2, customerId.toInt(), "pending"))
        orderDao.insert(createTestOrderWithStatus(3, customerId.toInt(), "completed"))
        orderDao.insert(createTestOrderWithStatus(4, customerId.toInt(), "cancelled"))
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val statusCounts = orderDao.getOrderStatusCounts(startTime, endTime)
        
        assertEquals("Should have 3 different statuses", 3, statusCounts.size)
        
        val pendingCount = statusCounts.find { it.status == "pending" }?.count ?: 0
        val completedCount = statusCounts.find { it.status == "completed" }?.count ?: 0
        val cancelledCount = statusCounts.find { it.status == "cancelled" }?.count ?: 0
        
        assertEquals("Should have 2 pending orders", 2, pendingCount)
        assertEquals("Should have 1 completed order", 1, completedCount)
        assertEquals("Should have 1 cancelled order", 1, cancelledCount)
    }
    
    @Test
    fun getOrderTypeCounts_returnsCorrectCounts() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        
        // Insert orders with different types
        orderDao.insert(createTestOrderWithType(1, customerId.toInt(), "dine_in", 100.0))
        orderDao.insert(createTestOrderWithType(2, customerId.toInt(), "dine_in", 150.0))
        orderDao.insert(createTestOrderWithType(3, customerId.toInt(), "takeaway", 75.0))
        orderDao.insert(createTestOrderWithType(4, customerId.toInt(), "delivery", 200.0))
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val typeCounts = orderDao.getOrderTypeCounts(startTime, endTime)
        
        assertEquals("Should have 3 different types", 3, typeCounts.size)
        
        val dineInStats = typeCounts.find { it.type == "dine_in" }
        val takeawayStats = typeCounts.find { it.type == "takeaway" }
        val deliveryStats = typeCounts.find { it.type == "delivery" }
        
        assertNotNull("Dine-in stats should exist", dineInStats)
        assertEquals("Should have 2 dine-in orders", 2, dineInStats!!.count)
        assertEquals("Dine-in revenue should be 250.0", 250.0, dineInStats.revenue, 0.01)
        
        assertNotNull("Takeaway stats should exist", takeawayStats)
        assertEquals("Should have 1 takeaway order", 1, takeawayStats!!.count)
        assertEquals("Takeaway revenue should be 75.0", 75.0, takeawayStats.revenue, 0.01)
        
        assertNotNull("Delivery stats should exist", deliveryStats)
        assertEquals("Should have 1 delivery order", 1, deliveryStats!!.count)
        assertEquals("Delivery revenue should be 200.0", 200.0, deliveryStats.revenue, 0.01)
    }
    
    @Test
    fun getHourlySales_returnsCorrectData() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        
        val currentTime = System.currentTimeMillis()
        val oneHourAgo = currentTime - (60 * 60 * 1000)
        val twoHoursAgo = currentTime - (2 * 60 * 60 * 1000)
        
        // Insert orders at different times
        orderDao.insert(createTestOrderWithTime(1, customerId.toInt(), 100.0, currentTime))
        orderDao.insert(createTestOrderWithTime(2, customerId.toInt(), 150.0, oneHourAgo))
        orderDao.insert(createTestOrderWithTime(3, customerId.toInt(), 75.0, twoHoursAgo))
        
        val startTime = currentTime - (3 * 60 * 60 * 1000) // 3 hours ago
        val endTime = currentTime + (60 * 60 * 1000) // 1 hour from now
        
        val hourlySales = orderDao.getHourlySales(startTime, endTime)
        
        assertTrue("Should have hourly sales data", hourlySales.isNotEmpty())
        
        val totalRevenue = hourlySales.sumOf { it.revenue }
        assertEquals("Total revenue should be 325.0", 325.0, totalRevenue, 0.01)
        
        val totalOrders = hourlySales.sumOf { it.orderCount }
        assertEquals("Total orders should be 3", 3, totalOrders)
    }
    
    // Helper methods to create test data
    private fun createTestCustomer(): Customer {
        return Customer(
            name = "Test Customer",
            phone = "+1234567890",
            address = "123 Test St",
            createdAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestProduct(): Product {
        return Product(
            nameEn = "Test Product",
            nameAr = "منتج تجريبي",
            descriptionEn = "Test product description",
            descriptionAr = "وصف المنتج التجريبي",
            price = 50.0,
            category = "Test Category",
            isActive = true,
            imageUrl = null
        )
    }
    
    private fun createTestOrder(orderNumber: Int, customerId: Int, subtotal: Double, tax: Double, total: Double): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = "dine_in",
            status = "completed",
            subtotal = subtotal,
            taxAmount = tax,
            discount = 0.0,
            total = total,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestOrderWithStatus(orderNumber: Int, customerId: Int, status: String): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = "dine_in",
            status = status,
            subtotal = 100.0,
            taxAmount = 10.0,
            discount = 0.0,
            total = 110.0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestOrderWithType(orderNumber: Int, customerId: Int, type: String, total: Double): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = type,
            status = "completed",
            subtotal = total - (total * 0.1),
            taxAmount = total * 0.1,
            discount = 0.0,
            total = total,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestOrderWithTime(orderNumber: Int, customerId: Int, total: Double, createdAt: Long): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = "dine_in",
            status = "completed",
            subtotal = total - (total * 0.1),
            taxAmount = total * 0.1,
            discount = 0.0,
            total = total,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    }
    
    private fun createTestOrderItem(orderId: Int, productId: Int, quantity: Double, unitPrice: Double): OrderItem {
        return OrderItem(
            orderId = orderId,
            productId = productId,
            quantity = quantity,
            unitPrice = unitPrice,
            subtotal = quantity * unitPrice,
            notes = ""
        )
    }
}
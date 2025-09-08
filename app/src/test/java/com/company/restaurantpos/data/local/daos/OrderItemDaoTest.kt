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
class OrderItemDaoTest {
    
    private lateinit var database: RestaurantDatabase
    private lateinit var orderDao: OrderDao
    private lateinit var customerDao: CustomerDao
    private lateinit var productDao: ProductDao
    private lateinit var orderItemDao: OrderItemDao
    
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
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun getTopSellingProductsWithDetails_returnsCorrectData() = runTest {
        // Insert test data
        val customerId = customerDao.insert(createTestCustomer())
        
        val product1Id = productDao.insert(createTestProduct("Burger", "برجر", "Burgers", 15.99))
        val product2Id = productDao.insert(createTestProduct("Pizza", "بيتزا", "Pizza", 22.50))
        val product3Id = productDao.insert(createTestProduct("Salad", "سلطة", "Salads", 12.99))
        
        val order1Id = orderDao.insert(createTestOrder(1, customerId.toInt()))
        val order2Id = orderDao.insert(createTestOrder(2, customerId.toInt()))
        val order3Id = orderDao.insert(createTestOrder(3, customerId.toInt()))
        
        // Insert order items - Burger sold 5 times, Pizza 3 times, Salad 2 times
        orderItemDao.insert(createTestOrderItem(order1Id.toInt(), product1Id.toInt(), 3.0, 15.99)) // Burger: 3 qty
        orderItemDao.insert(createTestOrderItem(order2Id.toInt(), product1Id.toInt(), 2.0, 15.99)) // Burger: 2 qty
        orderItemDao.insert(createTestOrderItem(order1Id.toInt(), product2Id.toInt(), 2.0, 22.50)) // Pizza: 2 qty
        orderItemDao.insert(createTestOrderItem(order3Id.toInt(), product2Id.toInt(), 1.0, 22.50)) // Pizza: 1 qty
        orderItemDao.insert(createTestOrderItem(order2Id.toInt(), product3Id.toInt(), 2.0, 12.99)) // Salad: 2 qty
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000) // 24 hours ago
        val endTime = currentTime + (60 * 60 * 1000) // 1 hour from now
        
        val topProducts = orderItemDao.getTopSellingProductsWithDetails(startTime, endTime, 10)
        
        assertEquals("Should return 3 products", 3, topProducts.size)
        
        // Check that products are ordered by quantity (descending)
        val burger = topProducts[0]
        val pizza = topProducts[1]
        val salad = topProducts[2]
        
        assertEquals("First product should be Burger", "Burger", burger.nameEn)
        assertEquals("Burger total quantity should be 5.0", 5.0, burger.totalQty, 0.01)
        assertEquals("Burger total revenue should be 79.95", 79.95, burger.totalRevenue, 0.01)
        assertEquals("Burger average price should be 15.99", 15.99, burger.averagePrice, 0.01)
        assertEquals("Burger category should be Burgers", "Burgers", burger.category)
        
        assertEquals("Second product should be Pizza", "Pizza", pizza.nameEn)
        assertEquals("Pizza total quantity should be 3.0", 3.0, pizza.totalQty, 0.01)
        assertEquals("Pizza total revenue should be 67.50", 67.50, pizza.totalRevenue, 0.01)
        
        assertEquals("Third product should be Salad", "Salad", salad.nameEn)
        assertEquals("Salad total quantity should be 2.0", 2.0, salad.totalQty, 0.01)
        assertEquals("Salad total revenue should be 25.98", 25.98, salad.totalRevenue, 0.01)
    }
    
    @Test
    fun getTopSellingProductsWithDetails_withLimit_returnsLimitedResults() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        
        // Insert 5 products
        val productIds = mutableListOf<Long>()
        for (i in 1..5) {
            val productId = productDao.insert(createTestProduct("Product $i", "منتج $i", "Category", 10.0 + i))
            productIds.add(productId)
        }
        
        val orderId = orderDao.insert(createTestOrder(1, customerId.toInt()))
        
        // Insert order items for all products
        productIds.forEachIndexed { index, productId ->
            orderItemDao.insert(createTestOrderItem(orderId.toInt(), productId.toInt(), (5 - index).toDouble(), 10.0 + index + 1))
        }
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val topProducts = orderItemDao.getTopSellingProductsWithDetails(startTime, endTime, 3)
        
        assertEquals("Should return only 3 products due to limit", 3, topProducts.size)
        
        // Check that results are ordered by quantity descending
        assertTrue("First product should have highest quantity", topProducts[0].totalQty >= topProducts[1].totalQty)
        assertTrue("Second product should have higher quantity than third", topProducts[1].totalQty >= topProducts[2].totalQty)
    }
    
    @Test
    fun getTopSellingProductsWithDetails_withNoData_returnsEmptyList() = runTest {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val topProducts = orderItemDao.getTopSellingProductsWithDetails(startTime, endTime, 10)
        
        assertTrue("Should return empty list when no data", topProducts.isEmpty())
    }
    
    @Test
    fun getTopSellingProductsWithDetails_withDateRange_filtersCorrectly() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        val productId = productDao.insert(createTestProduct("Test Product", "منتج تجريبي", "Test", 15.0))
        
        val currentTime = System.currentTimeMillis()
        val twoDaysAgo = currentTime - (2 * 24 * 60 * 60 * 1000)
        val oneDayAgo = currentTime - (24 * 60 * 60 * 1000)
        
        // Insert orders at different times
        val oldOrderId = orderDao.insert(createTestOrderWithTime(1, customerId.toInt(), twoDaysAgo))
        val recentOrderId = orderDao.insert(createTestOrderWithTime(2, customerId.toInt(), oneDayAgo))
        
        // Insert order items
        orderItemDao.insert(createTestOrderItem(oldOrderId.toInt(), productId.toInt(), 5.0, 15.0))
        orderItemDao.insert(createTestOrderItem(recentOrderId.toInt(), productId.toInt(), 3.0, 15.0))
        
        // Query for last 24 hours only
        val startTime = currentTime - (25 * 60 * 60 * 1000) // 25 hours ago
        val endTime = currentTime + (60 * 60 * 1000)
        
        val topProducts = orderItemDao.getTopSellingProductsWithDetails(startTime, endTime, 10)
        
        assertEquals("Should return 1 product", 1, topProducts.size)
        assertEquals("Should only include recent order quantity", 3.0, topProducts[0].totalQty, 0.01)
        assertEquals("Should only include recent order revenue", 45.0, topProducts[0].totalRevenue, 0.01)
    }
    
    @Test
    fun getProductSales_returnsCorrectAggregation() = runTest {
        val customerId = customerDao.insert(createTestCustomer())
        val productId = productDao.insert(createTestProduct("Test Product", "منتج تجريبي", "Test", 20.0))
        
        val order1Id = orderDao.insert(createTestOrder(1, customerId.toInt()))
        val order2Id = orderDao.insert(createTestOrder(2, customerId.toInt()))
        
        // Insert multiple order items for the same product
        orderItemDao.insert(createTestOrderItem(order1Id.toInt(), productId.toInt(), 2.0, 20.0))
        orderItemDao.insert(createTestOrderItem(order2Id.toInt(), productId.toInt(), 3.0, 20.0))
        
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (24 * 60 * 60 * 1000)
        val endTime = currentTime + (60 * 60 * 1000)
        
        val productSales = orderItemDao.getProductSales(startTime, endTime)
        
        assertEquals("Should return 1 product", 1, productSales.size)
        assertEquals("Product ID should match", productId.toInt(), productSales[0].productId)
        assertEquals("Total quantity should be 5.0", 5.0, productSales[0].totalQty, 0.01)
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
    
    private fun createTestProduct(nameEn: String, nameAr: String, category: String, price: Double): Product {
        return Product(
            nameEn = nameEn,
            nameAr = nameAr,
            descriptionEn = "$nameEn description",
            descriptionAr = "وصف $nameAr",
            price = price,
            category = category,
            isActive = true,
            imageUrl = null
        )
    }
    
    private fun createTestOrder(orderNumber: Int, customerId: Int): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = "dine_in",
            status = "completed",
            subtotal = 100.0,
            taxAmount = 10.0,
            discount = 0.0,
            total = 110.0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestOrderWithTime(orderNumber: Int, customerId: Int, createdAt: Long): Order {
        return Order(
            orderNumber = "ORD-${orderNumber.toString().padStart(3, '0')}",
            customerId = customerId,
            type = "dine_in",
            status = "completed",
            subtotal = 100.0,
            taxAmount = 10.0,
            discount = 0.0,
            total = 110.0,
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
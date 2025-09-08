package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.entities.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class PrintManagerTest {
    
    private lateinit var printManager: PrintManager
    
    @Before
    fun setup() {
        printManager = PrintManager()
    }
    
    @Test
    fun `initializePrinter with STUB should return true`() = runTest {
        val result = printManager.initializePrinter("STUB")
        assertTrue("STUB printer should initialize successfully", result)
    }
    
    @Test
    fun `initializePrinter with invalid connection should return false`() = runTest {
        val result = printManager.initializePrinter("INVALID_CONNECTION")
        assertFalse("Invalid connection should fail to initialize", result)
    }
    
    @Test
    fun `testPrint should return true when printer is initialized`() = runTest {
        // Initialize printer first
        printManager.initializePrinter("STUB")
        
        val result = printManager.testPrint()
        assertTrue("Test print should succeed when printer is initialized", result)
    }
    
    @Test
    fun `testPrint should return false when printer is not initialized`() = runTest {
        val result = printManager.testPrint()
        assertFalse("Test print should fail when printer is not initialized", result)
    }
    
    @Test
    fun `printCustomerReceipt should return true with valid data`() = runTest {
        // Initialize printer
        printManager.initializePrinter("STUB")
        
        // Create test data
        val order = createTestOrder()
        val orderItems = createTestOrderItems()
        val customer = createTestCustomer()
        val payments = createTestPayments()
        
        val result = printManager.printCustomerReceipt(order, orderItems, customer, payments)
        assertTrue("Customer receipt should print successfully with valid data", result)
    }
    
    @Test
    fun `printCustomerReceipt should return false when printer not initialized`() = runTest {
        val order = createTestOrder()
        val orderItems = createTestOrderItems()
        
        val result = printManager.printCustomerReceipt(order, orderItems, null, emptyList())
        assertFalse("Customer receipt should fail when printer not initialized", result)
    }
    
    @Test
    fun `printKitchenTicket should return true with valid data`() = runTest {
        // Initialize printer
        printManager.initializePrinter("STUB")
        
        val order = createTestOrder()
        val orderItems = createTestOrderItems()
        
        val result = printManager.printKitchenTicket(order, orderItems)
        assertTrue("Kitchen ticket should print successfully with valid data", result)
    }
    
    @Test
    fun `printKitchenTicket should return false when printer not initialized`() = runTest {
        val order = createTestOrder()
        val orderItems = createTestOrderItems()
        
        val result = printManager.printKitchenTicket(order, orderItems)
        assertFalse("Kitchen ticket should fail when printer not initialized", result)
    }
    
    @Test
    fun `printCustomerReceipt should handle null customer gracefully`() = runTest {
        printManager.initializePrinter("STUB")
        
        val order = createTestOrder()
        val orderItems = createTestOrderItems()
        
        val result = printManager.printCustomerReceipt(order, orderItems, null, emptyList())
        assertTrue("Customer receipt should print successfully even with null customer", result)
    }
    
    @Test
    fun `printCustomerReceipt should handle empty order items`() = runTest {
        printManager.initializePrinter("STUB")
        
        val order = createTestOrder()
        
        val result = printManager.printCustomerReceipt(order, emptyList(), null, emptyList())
        assertTrue("Customer receipt should handle empty order items", result)
    }
    
    @Test
    fun `printKitchenTicket should handle empty order items`() = runTest {
        printManager.initializePrinter("STUB")
        
        val order = createTestOrder()
        
        val result = printManager.printKitchenTicket(order, emptyList())
        assertTrue("Kitchen ticket should handle empty order items", result)
    }
    
    @Test
    fun `printCustomerReceipt should handle special characters in product names`() = runTest {
        printManager.initializePrinter("STUB")
        
        val order = createTestOrder()
        val orderItems = listOf(
            OrderItemWithProduct(
                orderItem = OrderItem(
                    id = 1,
                    orderId = 1,
                    productId = 1,
                    quantity = 1.0,
                    unitPrice = 15.99,
                    subtotal = 15.99,
                    notes = "Extra spicy! & special chars: @#$%"
                ),
                product = Product(
                    id = 1,
                    nameEn = "Spicy Chicken & Rice (Special)",
                    nameAr = "دجاج حار مع الأرز",
                    descriptionEn = "Spicy chicken with rice & vegetables",
                    descriptionAr = "دجاج حار مع الأرز والخضار",
                    price = 15.99,
                    category = "Main Course",
                    isActive = true,
                    imageUrl = null
                )
            )
        )
        
        val result = printManager.printCustomerReceipt(order, orderItems, null, emptyList())
        assertTrue("Customer receipt should handle special characters", result)
    }
    
    // Helper methods to create test data
    private fun createTestOrder(): Order {
        return Order(
            id = 1,
            orderNumber = "ORD-001",
            customerId = 1,
            type = "dine_in",
            status = "completed",
            subtotal = 25.98,
            taxAmount = 2.60,
            discount = 0.0,
            total = 28.58,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestOrderItems(): List<OrderItemWithProduct> {
        return listOf(
            OrderItemWithProduct(
                orderItem = OrderItem(
                    id = 1,
                    orderId = 1,
                    productId = 1,
                    quantity = 2.0,
                    unitPrice = 12.99,
                    subtotal = 25.98,
                    notes = "No onions"
                ),
                product = Product(
                    id = 1,
                    nameEn = "Chicken Burger",
                    nameAr = "برجر دجاج",
                    descriptionEn = "Grilled chicken burger with lettuce and tomato",
                    descriptionAr = "برجر دجاج مشوي مع الخس والطماطم",
                    price = 12.99,
                    category = "Burgers",
                    isActive = true,
                    imageUrl = null
                )
            )
        )
    }
    
    private fun createTestCustomer(): Customer {
        return Customer(
            id = 1,
            name = "John Doe",
            phone = "+1234567890",
            address = "123 Main St, City, State 12345",
            createdAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestPayments(): List<Payment> {
        return listOf(
            Payment(
                id = 1,
                orderId = 1,
                method = "cash",
                amount = 28.58,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
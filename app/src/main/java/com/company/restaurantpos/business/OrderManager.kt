package com.company.restaurantpos.business

import com.company.restaurantpos.data.local.daos.OrderDao
import com.company.restaurantpos.data.local.daos.OrderItemDao
import com.company.restaurantpos.data.local.daos.PaymentDao
import com.company.restaurantpos.data.local.entities.Order
import com.company.restaurantpos.data.local.entities.OrderItem
import com.company.restaurantpos.data.local.entities.Payment
import com.company.restaurantpos.ui.models.CartItem
import com.company.restaurantpos.ui.models.OrderType
import com.company.restaurantpos.ui.models.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages order creation and persistence
 */
@Singleton
class OrderManager @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val paymentDao: PaymentDao,
    private val recipeManager: RecipeManager
) {
    
    /**
     * Creates a complete order with items and payment
     * Returns the order ID if successful, null otherwise
     */
    suspend fun createOrder(
        cartItems: List<CartItem>,
        customerId: Long?,
        orderType: OrderType,
        paymentMethod: PaymentMethod,
        subtotal: Double,
        taxAmount: Double,
        discount: Double,
        total: Double
    ): Long? = withContext(Dispatchers.IO) {
        try {
            // Validate stock first
            val stockValidation = recipeManager.validateStock(cartItems)
            if (!stockValidation.isValid) {
                return@withContext null
            }
            
            // Generate unique order number
            val orderNo = generateOrderNumber()
            
            // Create order
            val order = Order(
                orderNo = orderNo,
                customerId = customerId,
                type = orderType.value,
                status = "new",
                subtotal = subtotal,
                tax = taxAmount,
                discount = discount,
                total = total,
                createdAt = System.currentTimeMillis()
            )
            
            val orderId = orderDao.insert(order)
            
            // Create order items
            for (cartItem in cartItems) {
                val orderItem = OrderItem(
                    orderId = orderId,
                    productId = cartItem.product.id,
                    qty = cartItem.quantity,
                    price = cartItem.unitPrice,
                    notes = cartItem.notes
                )
                orderItemDao.insert(orderItem)
            }
            
            // Create payment
            val payment = Payment(
                orderId = orderId,
                amount = total,
                method = paymentMethod.value,
                createdAt = System.currentTimeMillis()
            )
            paymentDao.insert(payment)
            
            // Deduct stock for recipe items
            val stockDeducted = recipeManager.deductStock(cartItems)
            if (!stockDeducted) {
                // In a real app, we would rollback the transaction here
                // For now, we'll log the error and continue
                // TODO: Implement proper transaction rollback
            }
            
            // Update order status to paid
            orderDao.updateStatus(orderId, "paid")
            
            orderId
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Generates a unique order number
     */
    private suspend fun generateOrderNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePrefix = dateFormat.format(Date())
        
        // Get the count of orders today to generate sequence number
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val todayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        
        val todayOrderCount = orderDao.getOrderCountByDateRange(todayStart, todayEnd)
        val sequenceNumber = String.format("%04d", todayOrderCount + 1)
        
        return "ORD$datePrefix$sequenceNumber"
    }
    
    /**
     * Gets order details by ID
     */
    suspend fun getOrderById(orderId: Long): OrderDetails? = withContext(Dispatchers.IO) {
        try {
            val order = orderDao.getById(orderId) ?: return@withContext null
            val orderItems = orderItemDao.getByOrderId(orderId)
            val payments = paymentDao.getByOrderId(orderId)
            
            OrderDetails(
                order = order,
                items = orderItems,
                payments = payments
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Complete order details including items and payments
 */
data class OrderDetails(
    val order: Order,
    val items: List<OrderItem>,
    val payments: List<Payment>
)
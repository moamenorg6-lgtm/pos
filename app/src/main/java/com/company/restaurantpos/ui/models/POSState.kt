package com.company.restaurantpos.ui.models

import com.company.restaurantpos.data.local.entities.Customer
import com.company.restaurantpos.data.local.entities.Product

/**
 * UI state for the POS screen
 */
data class POSState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val selectedCustomer: Customer? = null,
    val customerPhone: String = "",
    val customerName: String = "",
    val customerAddress: String = "",
    val showCreateCustomer: Boolean = false,
    val discount: Double = 0.0,
    val taxRate: Double = 0.15, // 15% tax
    val orderType: OrderType = OrderType.DINE_IN,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCheckoutDialog: Boolean = false,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH
) {
    val subtotal: Double
        get() = cartItems.sumOf { it.totalPrice }
    
    val taxAmount: Double
        get() = subtotal * taxRate
    
    val total: Double
        get() = subtotal + taxAmount - discount
    
    val cartItemCount: Int
        get() = cartItems.size
}

enum class OrderType(val value: String) {
    DINE_IN("dine_in"),
    TAKEAWAY("takeaway"),
    DELIVERY("delivery")
}

enum class PaymentMethod(val value: String) {
    CASH("cash"),
    CARD("card")
}
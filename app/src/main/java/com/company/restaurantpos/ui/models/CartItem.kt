package com.company.restaurantpos.ui.models

import com.company.restaurantpos.data.local.entities.Product

/**
 * Represents an item in the shopping cart
 */
data class CartItem(
    val product: Product,
    val quantity: Double = 1.0,
    val notes: String = "",
    val unitPrice: Double = product.price
) {
    val totalPrice: Double
        get() = quantity * unitPrice
    
    val displayName: String
        get() = product.nameEn // TODO: Add locale-based selection
}
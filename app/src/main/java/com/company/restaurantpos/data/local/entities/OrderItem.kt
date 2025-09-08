package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * OrderItem entity representing individual items within an order
 * 
 * @property id Unique order item identifier (auto-generated)
 * @property orderId Foreign key to the parent order
 * @property productId Foreign key to the ordered product
 * @property qty Quantity ordered
 * @property price Price per unit at time of order
 * @property notes Optional special instructions or notes
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["productId"])
    ]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderId: Int,
    
    val productId: Int,
    
    val qty: Double,
    
    val price: Double,
    
    val notes: String? = null
)
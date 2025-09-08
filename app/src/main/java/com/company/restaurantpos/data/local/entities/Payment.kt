package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Payment entity representing payments made for orders
 * 
 * @property id Unique payment identifier (auto-generated)
 * @property orderId Foreign key to the order being paid
 * @property amount Payment amount
 * @property method Payment method: "cash", "card", "mobile", etc.
 * @property createdAt Payment timestamp
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["createdAt"])
    ]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderId: Int,
    
    val amount: Double,
    
    val method: String, // cash, card, mobile, etc.
    
    val createdAt: Long = System.currentTimeMillis()
)
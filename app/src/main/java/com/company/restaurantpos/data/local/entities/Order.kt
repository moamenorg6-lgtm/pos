package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Order entity representing customer orders
 * 
 * @property id Unique order identifier (auto-generated)
 * @property orderNo Human-readable order number (unique)
 * @property customerId Foreign key to customer (nullable for walk-in orders)
 * @property type Order type: "dine_in", "takeaway", "delivery"
 * @property status Order status: "new", "preparing", "ready", "delivered", "paid", "cancelled"
 * @property total Total order amount
 * @property createdAt Order creation timestamp
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["orderNo"], unique = true),
        Index(value = ["customerId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderNo: String,
    
    val customerId: Int? = null,
    
    val type: String, // dine_in, takeaway, delivery
    
    val status: String, // new, preparing, ready, delivered, paid, cancelled
    
    val total: Double,
    
    val createdAt: Long = System.currentTimeMillis()
)
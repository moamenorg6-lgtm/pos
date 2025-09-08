package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Customer entity representing restaurant customers
 * 
 * @property id Unique customer identifier (auto-generated)
 * @property name Customer's full name
 * @property phone Customer's phone number (unique)
 * @property address Customer's delivery address
 * @property notes Optional notes about the customer
 */
@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["phone"], unique = true)
    ]
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    
    val phone: String,
    
    val address: String,
    
    val notes: String? = null
)
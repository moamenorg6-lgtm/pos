package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Product entity representing menu items and inventory products
 * 
 * @property id Unique product identifier (auto-generated)
 * @property nameEn Product name in English
 * @property nameAr Product name in Arabic
 * @property sku Stock Keeping Unit (optional)
 * @property price Selling price
 * @property cost Cost price for profit calculation
 * @property category Product category (e.g., "beverages", "main_course")
 * @property stock Current stock quantity
 * @property isActive Whether the product is available for sale
 * @property isRecipe Whether this product is made from a recipe
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["category"]),
        Index(value = ["isActive"])
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val nameEn: String,
    
    val nameAr: String,
    
    val sku: String? = null,
    
    val price: Double,
    
    val cost: Double,
    
    val category: String,
    
    val stock: Double,
    
    val isActive: Boolean = true,
    
    val isRecipe: Boolean = false
)
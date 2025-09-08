package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ingredient entity representing raw materials used in recipes
 * 
 * @property id Unique ingredient identifier (auto-generated)
 * @property nameEn Ingredient name in English
 * @property nameAr Ingredient name in Arabic
 * @property unit Unit of measurement (e.g., "kg", "liter", "piece")
 * @property stock Current stock quantity
 */
@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val nameEn: String,
    
    val nameAr: String,
    
    val unit: String,
    
    val stock: Double
)
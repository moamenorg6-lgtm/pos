package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Recipe entity representing cooking instructions for products
 * 
 * @property id Unique recipe identifier (auto-generated)
 * @property productId Foreign key to the product this recipe creates
 * @property instructionsEn Cooking instructions in English
 * @property instructionsAr Cooking instructions in Arabic
 */
@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"], unique = true)
    ]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val productId: Int,
    
    val instructionsEn: String,
    
    val instructionsAr: String
)
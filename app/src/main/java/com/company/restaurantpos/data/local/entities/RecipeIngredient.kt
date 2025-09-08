package com.company.restaurantpos.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * RecipeIngredient entity representing ingredients required for a recipe
 * This is a junction table with composite primary key
 * 
 * @property recipeId Foreign key to the recipe
 * @property ingredientId Foreign key to the ingredient
 * @property quantity Amount of ingredient needed
 * @property unit Unit of measurement for this specific usage
 */
@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["ingredientId"])
    ]
)
data class RecipeIngredient(
    val recipeId: Int,
    
    val ingredientId: Int,
    
    val quantity: Double,
    
    val unit: String
)
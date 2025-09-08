package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.RecipeIngredient
import com.company.restaurantpos.data.local.entities.Ingredient

/**
 * Data Access Object for RecipeIngredient entity
 * Provides methods for recipe ingredient-related database operations
 */
@Dao
interface RecipeIngredientDao {
    
    /**
     * Get all recipe ingredients for a specific recipe
     * @param recipeId Recipe ID
     * @return List of recipe ingredients
     */
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getByRecipeId(recipeId: Int): List<RecipeIngredient>
    
    /**
     * Get recipe ingredients with ingredient details for a specific recipe
     * @param recipeId Recipe ID
     * @return List of recipe ingredients with ingredient information
     */
    @Query("""
        SELECT ri.*, i.nameEn, i.nameAr, i.stock as ingredientStock
        FROM recipe_ingredients ri
        INNER JOIN ingredients i ON ri.ingredientId = i.id
        WHERE ri.recipeId = :recipeId
        ORDER BY i.nameEn ASC
    """)
    suspend fun getRecipeIngredientsWithDetails(recipeId: Int): List<RecipeIngredientWithDetails>
    
    /**
     * Get all recipes that use a specific ingredient
     * @param ingredientId Ingredient ID
     * @return List of recipe ingredients
     */
    @Query("SELECT * FROM recipe_ingredients WHERE ingredientId = :ingredientId")
    suspend fun getByIngredientId(ingredientId: Int): List<RecipeIngredient>
    
    /**
     * Check if recipe has sufficient ingredients in stock
     * @param recipeId Recipe ID
     * @return True if all ingredients are available in sufficient quantity
     */
    @Query("""
        SELECT COUNT(*) = 0 FROM recipe_ingredients ri
        INNER JOIN ingredients i ON ri.ingredientId = i.id
        WHERE ri.recipeId = :recipeId AND i.stock < ri.quantity
    """)
    suspend fun hasIngredientsInStock(recipeId: Int): Boolean
    
    /**
     * Get ingredients that are insufficient for a recipe
     * @param recipeId Recipe ID
     * @return List of recipe ingredients that don't have enough stock
     */
    @Query("""
        SELECT ri.*, i.nameEn, i.nameAr, i.stock as ingredientStock
        FROM recipe_ingredients ri
        INNER JOIN ingredients i ON ri.ingredientId = i.id
        WHERE ri.recipeId = :recipeId AND i.stock < ri.quantity
        ORDER BY i.nameEn ASC
    """)
    suspend fun getInsufficientIngredients(recipeId: Int): List<RecipeIngredientWithDetails>
    
    /**
     * Insert a new recipe ingredient
     * @param recipeIngredient Recipe ingredient to insert
     * @return Row ID of inserted recipe ingredient
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeIngredient: RecipeIngredient): Long
    
    /**
     * Insert multiple recipe ingredients
     * @param recipeIngredients List of recipe ingredients to insert
     * @return List of row IDs of inserted recipe ingredients
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipeIngredients: List<RecipeIngredient>): List<Long>
    
    /**
     * Update existing recipe ingredient
     * @param recipeIngredient Recipe ingredient to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(recipeIngredient: RecipeIngredient): Int
    
    /**
     * Delete recipe ingredient
     * @param recipeIngredient Recipe ingredient to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(recipeIngredient: RecipeIngredient): Int
    
    /**
     * Delete all recipe ingredients for a recipe
     * @param recipeId Recipe ID
     * @return Number of rows deleted
     */
    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteByRecipeId(recipeId: Int): Int
    
    /**
     * Delete all recipe ingredients for an ingredient
     * @param ingredientId Ingredient ID
     * @return Number of rows deleted
     */
    @Query("DELETE FROM recipe_ingredients WHERE ingredientId = :ingredientId")
    suspend fun deleteByIngredientId(ingredientId: Int): Int
}

/**
 * Data class for recipe ingredient with ingredient details
 */
data class RecipeIngredientWithDetails(
    val recipeId: Int,
    val ingredientId: Int,
    val quantity: Double,
    val unit: String,
    val nameEn: String,
    val nameAr: String,
    val ingredientStock: Double
)
package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Recipe

/**
 * Data Access Object for Recipe entity
 * Provides methods for recipe-related database operations
 */
@Dao
interface RecipeDao {
    
    /**
     * Get all recipes
     */
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>
    
    /**
     * Get recipe by ID
     * @param recipeId Recipe ID
     * @return Recipe if found, null otherwise
     */
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getById(recipeId: Int): Recipe?
    
    /**
     * Get recipe by product ID
     * @param productId Product ID
     * @return Recipe if found, null otherwise
     */
    @Query("SELECT * FROM recipes WHERE productId = :productId")
    suspend fun getByProductId(productId: Int): Recipe?
    
    /**
     * Insert a new recipe
     * @param recipe Recipe to insert
     * @return Row ID of inserted recipe
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(recipe: Recipe): Long
    
    /**
     * Update existing recipe
     * @param recipe Recipe to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(recipe: Recipe): Int
    
    /**
     * Delete recipe
     * @param recipe Recipe to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(recipe: Recipe): Int
    
    /**
     * Delete recipe by ID
     * @param recipeId Recipe ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteById(recipeId: Int): Int
    
    /**
     * Delete recipe by product ID
     * @param productId Product ID
     * @return Number of rows deleted
     */
    @Query("DELETE FROM recipes WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Int): Int
}
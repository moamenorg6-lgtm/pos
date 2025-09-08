package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Ingredient

/**
 * Data Access Object for Ingredient entity
 * Provides methods for ingredient-related database operations
 */
@Dao
interface IngredientDao {
    
    /**
     * Get all ingredients
     */
    @Query("SELECT * FROM ingredients ORDER BY nameEn ASC")
    suspend fun getAllIngredients(): List<Ingredient>
    
    /**
     * Get ingredient by ID
     * @param ingredientId Ingredient ID
     * @return Ingredient if found, null otherwise
     */
    @Query("SELECT * FROM ingredients WHERE id = :ingredientId")
    suspend fun getById(ingredientId: Int): Ingredient?
    
    /**
     * Search ingredients by name with multilingual support
     * @param query Search query
     * @param isArabic Whether to prioritize Arabic names
     * @return List of matching ingredients
     */
    @Query("""
        SELECT * FROM ingredients 
        WHERE nameEn LIKE '%' || :query || '%' 
           OR nameAr LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN :isArabic = 1 THEN nameAr ELSE nameEn END ASC
    """)
    suspend fun searchByName(query: String, isArabic: Boolean = false): List<Ingredient>
    
    /**
     * Get ingredients with low stock
     * @param threshold Stock threshold
     * @return List of ingredients with stock below threshold
     */
    @Query("SELECT * FROM ingredients WHERE stock <= :threshold ORDER BY stock ASC")
    suspend fun getLowStock(threshold: Double): List<Ingredient>
    
    /**
     * Update ingredient stock
     * @param ingredientId Ingredient ID
     * @param newStock New stock quantity
     * @return Number of rows updated
     */
    @Query("UPDATE ingredients SET stock = :newStock WHERE id = :ingredientId")
    suspend fun updateStock(ingredientId: Int, newStock: Double): Int
    
    /**
     * Reduce ingredient stock (for recipe preparation)
     * @param ingredientId Ingredient ID
     * @param quantity Quantity to reduce
     * @return Number of rows updated
     */
    @Query("UPDATE ingredients SET stock = stock - :quantity WHERE id = :ingredientId AND stock >= :quantity")
    suspend fun reduceStock(ingredientId: Int, quantity: Double): Int
    
    /**
     * Increase ingredient stock (for restocking)
     * @param ingredientId Ingredient ID
     * @param quantity Quantity to add
     * @return Number of rows updated
     */
    @Query("UPDATE ingredients SET stock = stock + :quantity WHERE id = :ingredientId")
    suspend fun increaseStock(ingredientId: Int, quantity: Double): Int
    
    /**
     * Insert a new ingredient
     * @param ingredient Ingredient to insert
     * @return Row ID of inserted ingredient
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(ingredient: Ingredient): Long
    
    /**
     * Update existing ingredient
     * @param ingredient Ingredient to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(ingredient: Ingredient): Int
    
    /**
     * Delete ingredient
     * @param ingredient Ingredient to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(ingredient: Ingredient): Int
    
    /**
     * Delete ingredient by ID
     * @param ingredientId Ingredient ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM ingredients WHERE id = :ingredientId")
    suspend fun deleteById(ingredientId: Int): Int
    
    /**
     * Delete all ingredients
     * @return Number of rows deleted
     */
    @Query("DELETE FROM ingredients")
    suspend fun deleteAll(): Int
}
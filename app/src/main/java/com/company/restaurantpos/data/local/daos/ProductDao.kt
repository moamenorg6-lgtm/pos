package com.company.restaurantpos.data.local.daos

import androidx.room.*
import com.company.restaurantpos.data.local.entities.Product

/**
 * Data Access Object for Product entity
 * Provides methods for product-related database operations
 */
@Dao
interface ProductDao {
    
    /**
     * Get all products
     */
    @Query("SELECT * FROM products ORDER BY nameEn ASC")
    suspend fun getAllProducts(): List<Product>
    
    /**
     * Get all active products
     * @return List of active products
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY nameEn ASC")
    suspend fun getActiveProducts(): List<Product>
    
    /**
     * Search products by name or SKU with multilingual support
     * @param query Search query
     * @param isArabic Whether to prioritize Arabic names
     * @return List of matching products
     */
    @Query("""
        SELECT * FROM products 
        WHERE (nameEn LIKE '%' || :query || '%' 
               OR nameAr LIKE '%' || :query || '%' 
               OR sku LIKE '%' || :query || '%')
        AND isActive = 1
        ORDER BY 
            CASE WHEN :isArabic = 1 THEN nameAr ELSE nameEn END ASC
    """)
    suspend fun searchByNameOrSKU(query: String, isArabic: Boolean = false): List<Product>
    
    /**
     * Get products by category
     * @param category Product category
     * @return List of products in the category
     */
    @Query("SELECT * FROM products WHERE category = :category AND isActive = 1 ORDER BY nameEn ASC")
    suspend fun getByCategory(category: String): List<Product>
    
    /**
     * Get product by ID
     * @param productId Product ID
     * @return Product if found, null otherwise
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getById(productId: Int): Product?
    
    /**
     * Get product by SKU
     * @param sku Product SKU
     * @return Product if found, null otherwise
     */
    @Query("SELECT * FROM products WHERE sku = :sku")
    suspend fun getBySKU(sku: String): Product?
    
    /**
     * Get products with low stock
     * @param threshold Stock threshold
     * @return List of products with stock below threshold
     */
    @Query("SELECT * FROM products WHERE stock <= :threshold AND isActive = 1 ORDER BY stock ASC")
    suspend fun getLowStock(threshold: Double): List<Product>
    
    /**
     * Get recipe products (products that are made from recipes)
     * @return List of recipe products
     */
    @Query("SELECT * FROM products WHERE isRecipe = 1 AND isActive = 1 ORDER BY nameEn ASC")
    suspend fun getRecipeProducts(): List<Product>
    
    /**
     * Update product stock
     * @param productId Product ID
     * @param newStock New stock quantity
     * @return Number of rows updated
     */
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Double): Int
    
    /**
     * Insert a new product
     * @param product Product to insert
     * @return Row ID of inserted product
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: Product): Long
    
    /**
     * Update existing product
     * @param product Product to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(product: Product): Int
    
    /**
     * Delete product
     * @param product Product to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(product: Product): Int
    
    /**
     * Delete product by ID
     * @param productId Product ID to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteById(productId: Int): Int
    
    /**
     * Delete all products
     * @return Number of rows deleted
     */
    @Query("DELETE FROM products")
    suspend fun deleteAll(): Int
    
    /**
     * Get all active products
     * @return List of active products
     */
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY nameEn ASC")
    suspend fun getAllActive(): List<Product>
    
    /**
     * Search products by multilingual name
     * @param query Search query
     * @return List of matching products
     */
    @Query("SELECT * FROM products WHERE nameEn LIKE '%' || :query || '%' OR nameAr LIKE '%' || :query || '%' ORDER BY nameEn ASC")
    suspend fun searchMultilingual(query: String): List<Product>
}
package com.company.restaurantpos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.company.restaurantpos.data.local.entities.*
import com.company.restaurantpos.data.local.daos.*

/**
 * Room database for the Restaurant POS application
 * 
 * This database contains all entities and DAOs for the POS system including:
 * - Customer management
 * - Product catalog with multilingual support
 * - Ingredient inventory
 * - Recipe management with ingredient relationships
 * - Order processing and tracking
 * - Payment handling
 * - User authentication and roles
 */
@Database(
    entities = [
        Customer::class,
        Product::class,
        Ingredient::class,
        Recipe::class,
        RecipeIngredient::class,
        Order::class,
        OrderItem::class,
        Payment::class,
        User::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // Customer operations
    abstract fun customerDao(): CustomerDao
    
    // Product catalog operations
    abstract fun productDao(): ProductDao
    
    // Ingredient inventory operations
    abstract fun ingredientDao(): IngredientDao
    
    // Recipe management operations
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
    
    // Order processing operations
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    
    // Payment operations
    abstract fun paymentDao(): PaymentDao
    
    // User authentication operations
    abstract fun userDao(): UserDao
    
    companion object {
        const val DATABASE_NAME = "restaurant_pos_database"
    }
}
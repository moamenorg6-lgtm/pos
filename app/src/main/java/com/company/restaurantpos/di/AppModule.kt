package com.company.restaurantpos.di

import android.content.Context
import androidx.room.Room
import com.company.restaurantpos.data.local.AppDatabase
import com.company.restaurantpos.data.local.daos.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // For development - remove in production
        .build()
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    // Customer DAO
    @Provides
    fun provideCustomerDao(database: AppDatabase): CustomerDao = database.customerDao()

    // Product DAO
    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    // Ingredient DAO
    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao = database.ingredientDao()

    // Recipe DAOs
    @Provides
    fun provideRecipeDao(database: AppDatabase): RecipeDao = database.recipeDao()

    @Provides
    fun provideRecipeIngredientDao(database: AppDatabase): RecipeIngredientDao = database.recipeIngredientDao()

    // Order DAOs
    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()

    @Provides
    fun provideOrderItemDao(database: AppDatabase): OrderItemDao = database.orderItemDao()

    // Payment DAO
    @Provides
    fun providePaymentDao(database: AppDatabase): PaymentDao = database.paymentDao()

    // User DAO
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
    
    // Business Logic Classes are automatically provided by Hilt @Singleton annotation
    // RecipeManager, CustomerAutoFillManager, and OrderManager are injected automatically
}
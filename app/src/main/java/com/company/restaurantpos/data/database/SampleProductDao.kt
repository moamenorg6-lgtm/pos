package com.company.restaurantpos.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.restaurantpos.data.entity.SampleProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleProductDao {

    @Query("SELECT * FROM sample_products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<SampleProductEntity>>

    @Query("SELECT * FROM sample_products WHERE id = :id")
    suspend fun getProductById(id: Long): SampleProductEntity?

    @Query("SELECT * FROM sample_products WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailableProducts(): Flow<List<SampleProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: SampleProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<SampleProductEntity>)

    @Update
    suspend fun updateProduct(product: SampleProductEntity)

    @Delete
    suspend fun deleteProduct(product: SampleProductEntity)

    @Query("DELETE FROM sample_products")
    suspend fun deleteAllProducts()
}
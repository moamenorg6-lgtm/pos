package com.company.restaurantpos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_products")
data class SampleProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameAr: String,
    val price: Double,
    val category: String,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
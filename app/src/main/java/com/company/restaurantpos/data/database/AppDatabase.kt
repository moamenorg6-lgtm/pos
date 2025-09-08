package com.company.restaurantpos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.company.restaurantpos.data.entity.SampleProductEntity

@Database(
    entities = [SampleProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleProductDao(): SampleProductDao
}
package com.ashokvatika.app.data.inventory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlantEntity::class, PotEntity::class, SoilEntity::class, FertilizerEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AshokvatikaDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun potDao(): PotDao
    abstract fun soilDao(): SoilDao
    abstract fun fertilizerDao(): FertilizerDao

    companion object {
        @Volatile
        private var INSTANCE: AshokvatikaDatabase? = null

        fun getInstance(context: Context): AshokvatikaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AshokvatikaDatabase::class.java,
                    "ashokvatika.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}

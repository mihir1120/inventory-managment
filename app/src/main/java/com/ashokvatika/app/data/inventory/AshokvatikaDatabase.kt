package com.ashokvatika.app.data.inventory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(
    entities = [PlantEntity::class, PotEntity::class, SoilEntity::class, FertilizerEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AshokvatikaDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun potDao(): PotDao
    abstract fun soilDao(): SoilDao
    abstract fun fertilizerDao(): FertilizerDao

    companion object {
        @Volatile
        private var INSTANCE: AshokvatikaDatabase? = null

        // Version 5 is the current baseline schema. Add future migrations here
        // and bump the database version alongside a new exported schema JSON.
        private val MIGRATIONS: Array<Migration> = emptyArray()

        fun getInstance(context: Context): AshokvatikaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AshokvatikaDatabase::class.java,
                    "ashokvatika.db"
                ).addMigrations(*MIGRATIONS)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

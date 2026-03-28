package com.ashokvatika.app.data.inventory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY date DESC, title ASC")
    fun observeAll(): Flow<List<PlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PlantEntity)

    @Update
    suspend fun update(item: PlantEntity)
}

@Dao
interface PotDao {
    @Query("SELECT * FROM pots ORDER BY date DESC, title ASC")
    fun observeAll(): Flow<List<PotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PotEntity)

    @Update
    suspend fun update(item: PotEntity)
}

@Dao
interface SoilDao {
    @Query("SELECT * FROM soil ORDER BY date DESC, title ASC")
    fun observeAll(): Flow<List<SoilEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SoilEntity)

    @Update
    suspend fun update(item: SoilEntity)
}

@Dao
interface FertilizerDao {
    @Query("SELECT * FROM fertilizers ORDER BY date DESC, title ASC")
    fun observeAll(): Flow<List<FertilizerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FertilizerEntity)

    @Update
    suspend fun update(item: FertilizerEntity)
}

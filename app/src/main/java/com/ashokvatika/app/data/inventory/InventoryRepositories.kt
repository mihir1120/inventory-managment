package com.ashokvatika.app.data.inventory

import com.ashokvatika.app.model.InventoryRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlantsRepository(
    private val dao: PlantDao
) {
    val items: Flow<List<InventoryRecord>> = dao.observeAll().map { list -> list.map { it.toRecord() } }

    suspend fun add(item: InventoryRecord) {
        dao.insert(item.copy(id = 0).toPlantEntity())
    }

    suspend fun update(item: InventoryRecord) {
        dao.update(item.toPlantEntity())
    }
}

class PotsRepository(
    private val dao: PotDao
) {
    val items: Flow<List<InventoryRecord>> = dao.observeAll().map { list -> list.map { it.toRecord() } }

    suspend fun add(item: InventoryRecord) {
        dao.insert(item.copy(id = 0).toPotEntity())
    }

    suspend fun update(item: InventoryRecord) {
        dao.update(item.toPotEntity())
    }
}

class SoilRepository(
    private val dao: SoilDao
) {
    val items: Flow<List<InventoryRecord>> = dao.observeAll().map { list -> list.map { it.toRecord() } }

    suspend fun add(item: InventoryRecord) {
        dao.insert(item.copy(id = 0).toSoilEntity())
    }

    suspend fun update(item: InventoryRecord) {
        dao.update(item.toSoilEntity())
    }
}

class FertilizersRepository(
    private val dao: FertilizerDao
) {
    val items: Flow<List<InventoryRecord>> = dao.observeAll().map { list -> list.map { it.toRecord() } }

    suspend fun add(item: InventoryRecord) {
        dao.insert(item.copy(id = 0).toFertilizerEntity())
    }

    suspend fun update(item: InventoryRecord) {
        dao.update(item.toFertilizerEntity())
    }
}

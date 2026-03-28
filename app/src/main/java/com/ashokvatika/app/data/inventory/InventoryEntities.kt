package com.ashokvatika.app.data.inventory

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ashokvatika.app.model.InventoryRecord

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val vendor: String,
    val vendorPhone: String,
    val vendorEmail: String,
    val vendorAddress: String,
    val vendorWebsite: String,
    val imageLayout: String,
    val date: String,
    val photoUri: String?,
    val batch: Int,
    val sale: Int,
    val rent: Int,
    val qtn: Int
)

@Entity(tableName = "pots")
data class PotEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val vendor: String,
    val vendorPhone: String,
    val vendorEmail: String,
    val vendorAddress: String,
    val vendorWebsite: String,
    val imageLayout: String,
    val date: String,
    val photoUri: String?,
    val batch: Int,
    val sale: Int,
    val rent: Int,
    val qtn: Int
)

@Entity(tableName = "soil")
data class SoilEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val vendor: String,
    val vendorPhone: String,
    val vendorEmail: String,
    val vendorAddress: String,
    val vendorWebsite: String,
    val imageLayout: String,
    val date: String,
    val photoUri: String?,
    val batch: Int,
    val sale: Int,
    val rent: Int,
    val qtn: Int
)

@Entity(tableName = "fertilizers")
data class FertilizerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val vendor: String,
    val vendorPhone: String,
    val vendorEmail: String,
    val vendorAddress: String,
    val vendorWebsite: String,
    val imageLayout: String,
    val date: String,
    val photoUri: String?,
    val batch: Int,
    val sale: Int,
    val rent: Int,
    val qtn: Int
)

fun PlantEntity.toRecord(): InventoryRecord = InventoryRecord(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun PotEntity.toRecord(): InventoryRecord = InventoryRecord(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun SoilEntity.toRecord(): InventoryRecord = InventoryRecord(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun FertilizerEntity.toRecord(): InventoryRecord = InventoryRecord(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)

fun InventoryRecord.toPlantEntity(): PlantEntity = PlantEntity(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun InventoryRecord.toPotEntity(): PotEntity = PotEntity(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun InventoryRecord.toSoilEntity(): SoilEntity = SoilEntity(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)
fun InventoryRecord.toFertilizerEntity(): FertilizerEntity = FertilizerEntity(id, title, vendor, vendorPhone, vendorEmail, vendorAddress, vendorWebsite, imageLayout, date, photoUri, batch, sale, rent, qtn)

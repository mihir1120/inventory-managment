package com.ashokvatika.app.model

data class InventoryRecord(
    val id: Int = 0,
    val title: String,
    val vendor: String = "",
    val vendorPhone: String = "",
    val vendorEmail: String = "",
    val vendorAddress: String = "",
    val vendorWebsite: String = "",
    val imageLayout: String = "vertical",
    val date: String,
    val photoUri: String?,
    val batch: Int,
    val sale: Int,
    val rent: Int,
    val qtn: Int
) {
    val stock: Int
        get() = (batch - sale - rent).coerceAtLeast(0)
}

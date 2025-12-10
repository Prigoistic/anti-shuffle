package com.gallery_app.data.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_metadata")
data class ScanMetadata(
    @PrimaryKey val id: Int = 1,
    val lastScanTimestamp: Long,
    val lastScanHash: String
)

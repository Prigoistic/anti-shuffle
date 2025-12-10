package com.gallery_app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_items",
    indices = [
        Index(value = ["dateTaken"], name = "idx_date_taken"),
        Index(value = ["bucket"], name = "idx_bucket"),
        Index(value = ["bucket", "dateTaken"], name = "idx_bucket_date"),
        Index(value = ["addedTimestamp"], name = "idx_added_timestamp")
    ]
)
data class MediaEntity(
    @PrimaryKey val id: Long,
    val uri: String,
    val dateTaken: Long,
    val bucket: String,
    val size: Long,
    val width: Int = 0,
    val height: Int = 0,
    val orientation: Int = 0,
    val mimeType: String = "image/jpeg",
    val addedTimestamp: Long = System.currentTimeMillis()
)

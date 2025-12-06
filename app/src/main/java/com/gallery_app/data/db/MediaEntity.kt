package com.gallery_app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey val id: Long,
    val uri: String,
    val dateTaken: Long,
    val bucket: String,
    val size: Long
)

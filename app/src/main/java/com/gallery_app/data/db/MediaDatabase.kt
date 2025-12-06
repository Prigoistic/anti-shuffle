package com.gallery_app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MediaEntity::class],
    version = 1
)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}

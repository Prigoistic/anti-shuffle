package com.gallery_app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gallery_app.data.sync.ScanMetadata
import com.gallery_app.data.sync.ScanMetadataDao

@Database(
    entities = [MediaEntity::class, ScanMetadata::class],
    version = 4,
    exportSchema = false
)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun scanMetadataDao(): ScanMetadataDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add scan_metadata table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS scan_metadata (
                        id INTEGER PRIMARY KEY NOT NULL,
                        lastScanTimestamp INTEGER NOT NULL,
                        lastScanHash TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add indexes for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_date_taken ON media_items(dateTaken)")
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_bucket ON media_items(bucket)")
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_bucket_date ON media_items(bucket, dateTaken)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add metadata columns to media_items
                database.execSQL("ALTER TABLE media_items ADD COLUMN width INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE media_items ADD COLUMN height INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE media_items ADD COLUMN orientation INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE media_items ADD COLUMN mimeType TEXT NOT NULL DEFAULT 'image/jpeg'")
                database.execSQL("ALTER TABLE media_items ADD COLUMN addedTimestamp INTEGER NOT NULL DEFAULT 0")
                
                // Add index for recently added queries
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_added_timestamp ON media_items(addedTimestamp)")
            }
        }
    }
}

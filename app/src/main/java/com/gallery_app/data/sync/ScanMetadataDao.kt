package com.gallery_app.data.sync

import androidx.room.*

@Dao
interface ScanMetadataDao {
    @Query("SELECT * FROM scan_metadata WHERE id = 1 LIMIT 1")
    suspend fun getMetadata(): ScanMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMetadata(metadata: ScanMetadata)
}

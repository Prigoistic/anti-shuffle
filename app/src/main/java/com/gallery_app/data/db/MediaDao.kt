package com.gallery_app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY dateTaken DESC")
    fun getAll(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<MediaEntity?>

    @Query("SELECT * FROM media_items WHERE bucket = :bucketName ORDER BY dateTaken DESC")
    fun getByBucket(bucketName: String): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaEntity>)

    @Query("DELETE FROM media_items")
    suspend fun clear()
}

package com.gallery_app.data.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class BucketInfo(
    val bucket: String,
    val count: Int,
    val coverUri: String
)

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY dateTaken DESC")
    fun getAll(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items ORDER BY dateTaken DESC")
    fun getPagedMedia(): PagingSource<Int, MediaEntity>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<MediaEntity?>

    @Query("SELECT * FROM media_items WHERE bucket = :bucketName ORDER BY dateTaken DESC")
    fun getByBucket(bucketName: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE bucket = :bucketName ORDER BY dateTaken DESC")
    fun getPagedByBucket(bucketName: String): PagingSource<Int, MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaEntity>)

    @Query("DELETE FROM media_items")
    suspend fun clear()

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM media_items")
    suspend fun getCount(): Int

    @Query("SELECT id FROM media_items")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT * FROM media_items ORDER BY addedTimestamp DESC LIMIT :limit")
    fun getRecentlyAdded(limit: Int = 50): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE addedTimestamp >= :timestamp ORDER BY addedTimestamp DESC")
    fun getAddedSince(timestamp: Long): Flow<List<MediaEntity>>

    @Query("""
        SELECT bucket, 
               COUNT(*) as count, 
               (SELECT uri FROM media_items m2 WHERE m2.bucket = media_items.bucket ORDER BY m2.dateTaken DESC LIMIT 1) as coverUri
        FROM media_items 
        GROUP BY bucket 
        ORDER BY count DESC
    """)
    fun getBuckets(): Flow<List<BucketInfo>>
}

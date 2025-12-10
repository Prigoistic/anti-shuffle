package com.gallery_app.data.repository

import androidx.paging.PagingData
import com.gallery_app.data.db.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllMedia(): Flow<List<MediaEntity>>
    fun getPagedMedia(): Flow<PagingData<MediaEntity>>
    fun getById(id: Long): Flow<MediaEntity?>
    fun getByBucket(bucketName: String): Flow<List<MediaEntity>>
    fun getPagedByBucket(bucketName: String): Flow<PagingData<MediaEntity>>
    suspend fun insertAll(mediaList: List<MediaEntity>)
    suspend fun clear()
    suspend fun deleteById(id: Long)
    suspend fun deleteByIds(ids: List<Long>)
    suspend fun getCount(): Int
    suspend fun getAllIds(): List<Long>
}

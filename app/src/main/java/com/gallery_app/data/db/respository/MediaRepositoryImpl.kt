package com.gallery_app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gallery_app.data.db.MediaDao
import com.gallery_app.data.db.MediaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao
) : MediaRepository {

    override fun getAllMedia(): Flow<List<MediaEntity>> {
        return mediaDao.getAll()
    }

    override fun getPagedMedia(): Flow<PagingData<MediaEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 20,
                enablePlaceholders = true,
                initialLoadSize = 100
            ),
            pagingSourceFactory = { mediaDao.getPagedMedia() }
        ).flow
    }

    override fun getById(id: Long): Flow<MediaEntity?> {
        return mediaDao.getById(id)
    }

    override fun getByBucket(bucketName: String): Flow<List<MediaEntity>> {
        return mediaDao.getByBucket(bucketName)
    }

    override fun getPagedByBucket(bucketName: String): Flow<PagingData<MediaEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 20,
                enablePlaceholders = true,
                initialLoadSize = 100
            ),
            pagingSourceFactory = { mediaDao.getPagedByBucket(bucketName) }
        ).flow
    }

    override suspend fun insertAll(mediaList: List<MediaEntity>) {
        mediaDao.insertAll(mediaList)
    }

    override suspend fun clear() {
        mediaDao.clear()
    }

    override suspend fun deleteById(id: Long) {
        mediaDao.deleteById(id)
    }

    override suspend fun deleteByIds(ids: List<Long>) {
        mediaDao.deleteByIds(ids)
    }

    override suspend fun getCount(): Int {
        return mediaDao.getCount()
    }

    override suspend fun getAllIds(): List<Long> {
        return mediaDao.getAllIds()
    }
}

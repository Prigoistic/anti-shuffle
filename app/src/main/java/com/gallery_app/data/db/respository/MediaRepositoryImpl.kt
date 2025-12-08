package com.gallery_app.data.repository

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

    override fun getById(id: Long): Flow<MediaEntity?> {
        return mediaDao.getById(id)
    }

    override fun getByBucket(bucketName: String): Flow<List<MediaEntity>> {
        return mediaDao.getByBucket(bucketName)
    }

    override suspend fun insertAll(mediaList: List<MediaEntity>) {
        mediaDao.insertAll(mediaList)
    }

    override suspend fun clear() {
        mediaDao.clear()
    }
}

package com.gallery_app.data.repository

import com.gallery_app.data.db.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllMedia(): Flow<List<MediaEntity>>
    suspend fun insertAll(mediaList: List<MediaEntity>)
    suspend fun clear()
}

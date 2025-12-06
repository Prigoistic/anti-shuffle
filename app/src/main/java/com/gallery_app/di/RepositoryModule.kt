package com.gallery_app.di

import com.gallery_app.data.db.MediaDao
import com.gallery_app.data.repository.MediaRepository
import com.gallery_app.data.repository.MediaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMediaRepository(
        mediaDao: MediaDao
    ): MediaRepository = MediaRepositoryImpl(mediaDao)
}

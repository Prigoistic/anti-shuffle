package com.gallery_app.di

import android.content.Context
import androidx.room.Room
import com.gallery_app.data.db.MediaDatabase
import com.gallery_app.data.db.MediaDao
import com.gallery_app.data.sync.ScanMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MediaDatabase =
        Room.databaseBuilder(
            context,
            MediaDatabase::class.java,
            "media_db"
        )
        .addMigrations(
            MediaDatabase.MIGRATION_1_2,
            MediaDatabase.MIGRATION_2_3,
            MediaDatabase.MIGRATION_3_4
        )
        .fallbackToDestructiveMigration() // Fallback if migration fails
        .build()

    @Provides
    fun provideMediaDao(db: MediaDatabase): MediaDao = db.mediaDao()

    @Provides
    fun provideScanMetadataDao(db: MediaDatabase): ScanMetadataDao = db.scanMetadataDao()
}

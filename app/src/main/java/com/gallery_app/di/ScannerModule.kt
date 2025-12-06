package com.gallery_app.di

import android.content.Context
import com.gallery_app.data.MediaScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    @Singleton
    fun provideMediaScanner(
        @ApplicationContext context: Context
    ): MediaScanner = MediaScanner(context)
}

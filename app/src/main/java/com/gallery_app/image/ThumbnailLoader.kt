package com.gallery_app.image

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import coil.request.ImageRequest
import coil.size.Scale

object ThumbnailLoader {
    
    /**
     * Create an optimized thumbnail request for grid display
     */
    fun createThumbnailRequest(
        context: Context,
        mediaId: Long,
        targetSize: Int = 400
    ): ImageRequest {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore thumbnail API for Android 10+
            ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mediaId
            )
        } else {
            ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mediaId
            )
        }
        
        return ImageRequest.Builder(context)
            .data(uri)
            .size(targetSize, targetSize)
            .scale(Scale.FILL)
            .crossfade(150)
            .memoryCacheKey("thumb_$mediaId")
            .diskCacheKey("thumb_$mediaId")
            .build()
    }

    /**
     * Create an optimized full-size image request
     */
    fun createFullImageRequest(
        context: Context,
        mediaId: Long
    ): ImageRequest {
        val uri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            mediaId
        )
        
        return ImageRequest.Builder(context)
            .data(uri)
            .crossfade(200)
            .memoryCacheKey("full_$mediaId")
            .diskCacheKey("full_$mediaId")
            .build()
    }

    /**
     * Preload thumbnails for upcoming items
     */
    fun preloadThumbnails(
        context: Context,
        imageLoader: coil.ImageLoader,
        mediaIds: List<Long>
    ) {
        mediaIds.forEach { id ->
            val request = createThumbnailRequest(context, id)
            imageLoader.enqueue(request)
        }
    }
}

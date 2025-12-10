package com.gallery_app.data

import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GalleryImage(
    val id: Long,
    val uri: String,
    val dateTaken: Long,
    val bucketName: String,
    val size: Long,
)

class MediaScanner(private val context: Context) {

    suspend fun loadImages(): List<GalleryImage> =
        withContext(Dispatchers.IO) {

            val images = mutableListOf<GalleryImage>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->

                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val uriStr = "${uri}/$id"
                    val date = cursor.getLong(dateCol)
                    val bucket = cursor.getString(bucketCol) ?: "Unknown"
                    val size = cursor.getLong(sizeCol)

                    images.add(
                        GalleryImage(id, uriStr, date, bucket, size)
                    )
                }
            }

            images
        }
}

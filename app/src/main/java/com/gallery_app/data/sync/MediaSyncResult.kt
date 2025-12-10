package com.gallery_app.data.sync

import com.gallery_app.data.GalleryImage

data class MediaSyncResult(
    val newItems: List<GalleryImage>,
    val deletedIds: List<Long>,
    val updatedItems: List<GalleryImage>,
    val unchanged: Int
)

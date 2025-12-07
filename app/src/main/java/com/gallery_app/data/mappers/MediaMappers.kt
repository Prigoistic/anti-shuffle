package com.gallery_app.data.mappers

import com.gallery_app.data.GalleryImage
import com.gallery_app.data.db.MediaEntity

fun GalleryImage.toEntity() = MediaEntity(
    id = id,
    uri = uri,
    bucket = bucketName,
    dateTaken = dateTaken,
    size = size
)

fun MediaEntity.toGalleryImage() = GalleryImage(
    id = id,
    uri = uri,
    dateTaken = dateTaken,
    bucketName = bucket,
    size = size
)

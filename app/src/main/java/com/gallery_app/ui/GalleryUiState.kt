package com.gallery_app.ui

import com.gallery_app.data.GalleryImage

sealed interface GalleryUiState {
    object Loading : GalleryUiState
    data class Success(val images: List<GalleryImage>) : GalleryUiState
    object Empty : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

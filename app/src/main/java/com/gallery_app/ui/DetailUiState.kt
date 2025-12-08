package com.gallery_app.ui

import com.gallery_app.data.GalleryImage

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val media: GalleryImage) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

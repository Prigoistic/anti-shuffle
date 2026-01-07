package com.gallery_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.repository.MediaRepository
import com.gallery_app.data.mappers.toGalleryImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _mediaState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val mediaState: StateFlow<DetailUiState> = _mediaState
    
    private var loadedMediaId: Long? = null

    fun loadMedia(id: Long) {
        // Avoid reloading if already loaded
        if (loadedMediaId == id && _mediaState.value is DetailUiState.Success) {
            return
        }
        
        viewModelScope.launch {
            try {
                _mediaState.value = DetailUiState.Loading
                val entity = mediaRepository.getById(id).firstOrNull()
                if (entity != null) {
                    loadedMediaId = id
                    _mediaState.value = DetailUiState.Success(entity.toGalleryImage())
                } else {
                    _mediaState.value = DetailUiState.Error("Media not found")
                }
            } catch (e: Exception) {
                _mediaState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

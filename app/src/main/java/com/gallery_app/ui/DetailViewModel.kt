package com.gallery_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.repository.MediaRepository
import com.gallery_app.data.mappers.toGalleryImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _mediaState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val mediaState: StateFlow<DetailUiState> = _mediaState

    fun loadMedia(id: Long) {
        viewModelScope.launch {
            try {
                mediaRepository.getById(id).collectLatest { entity ->
                    if (entity != null) {
                        _mediaState.value = DetailUiState.Success(entity.toGalleryImage())
                    } else {
                        _mediaState.value = DetailUiState.Error("Media not found")
                    }
                }
            } catch (e: Exception) {
                _mediaState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

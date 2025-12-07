package com.gallery_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.GalleryImage
import com.gallery_app.data.MediaScanner
import com.gallery_app.data.repository.MediaRepository
import com.gallery_app.data.mappers.toEntity
import com.gallery_app.data.mappers.toGalleryImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val scanner: MediaScanner,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState

    init {
        observeDatabase()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            mediaRepository
                .getAllMedia()
                .map { entities -> entities.map { it.toGalleryImage() } }
                .collectLatest { list ->
                    _uiState.value = when {
                        list.isEmpty() -> GalleryUiState.Empty
                        else -> GalleryUiState.Success(list)
                    }
                }
        }
    }

    fun scanImages() {
        viewModelScope.launch {
            val loaded = scanner.loadImages()
            syncToDatabase(loaded)
        }
    }

    private fun syncToDatabase(list: List<GalleryImage>) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = list.map { it.toEntity() }
            mediaRepository.clear()
            mediaRepository.insertAll(entities)
        }
    }
}

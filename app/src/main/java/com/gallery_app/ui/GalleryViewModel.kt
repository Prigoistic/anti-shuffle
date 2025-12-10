package com.gallery_app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.gallery_app.data.GalleryImage
import com.gallery_app.data.MediaScanner
import com.gallery_app.data.repository.MediaRepository
import com.gallery_app.data.mappers.toEntity
import com.gallery_app.data.mappers.toGalleryImage
import com.gallery_app.data.sync.DifferentialSyncEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val scanner: MediaScanner,
    private val mediaRepository: MediaRepository,
    private val syncEngine: DifferentialSyncEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _syncStatus = MutableStateFlow("")
    val syncStatus: StateFlow<String> = _syncStatus

    val pagedMedia: Flow<PagingData<GalleryImage>> = mediaRepository
        .getPagedMedia()
        .map { pagingData ->
            pagingData.map { it.toGalleryImage() }
        }
        .cachedIn(viewModelScope)

    init {
        checkDatabaseState()
    }

    private fun checkDatabaseState() {
        viewModelScope.launch {
            val count = mediaRepository.getCount()
            _uiState.value = when {
                count == 0 -> GalleryUiState.Empty
                else -> GalleryUiState.Success(emptyList()) // Using paging now
            }
        }
    }

    fun scanImages(forceFull: Boolean = false) {
        viewModelScope.launch {
            _isScanning.value = true
            _uiState.value = GalleryUiState.Loading
            try {
                if (forceFull || syncEngine.shouldPerformFullSync()) {
                    Log.d("GalleryViewModel", "Performing FULL sync")
                    _syncStatus.value = "Full scan in progress..."
                    performFullSync()
                } else {
                    Log.d("GalleryViewModel", "Performing DIFFERENTIAL sync")
                    _syncStatus.value = "Checking for changes..."
                    performDifferentialSync()
                }
                checkDatabaseState()
                _syncStatus.value = ""
            } catch (e: Exception) {
                Log.e("GalleryViewModel", "Sync error", e)
                _uiState.value = GalleryUiState.Error(e.message ?: "Sync failed")
                _syncStatus.value = ""
            } finally {
                _isScanning.value = false
            }
        }
    }

    private suspend fun performFullSync() {
        val loaded = scanner.loadImages()
        val entities = loaded.map { it.toEntity() }
        mediaRepository.clear()
        mediaRepository.insertAll(entities)
        Log.d("GalleryViewModel", "Full sync: ${entities.size} items")
    }

    private suspend fun performDifferentialSync() {
        val result = syncEngine.performDifferentialSync()
        syncEngine.applySyncResult(result)
        _syncStatus.value = "New: ${result.newItems.size}, Deleted: ${result.deletedIds.size}, Updated: ${result.updatedItems.size}"
        Log.d("GalleryViewModel", _syncStatus.value)
    }
}

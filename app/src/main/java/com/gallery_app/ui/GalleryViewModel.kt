package com.gallery_app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.GalleryImage
import com.gallery_app.data.MediaScanner
import com.gallery_app.data.db.MediaEntity
import com.gallery_app.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val scanner: MediaScanner,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    var images by mutableStateOf<List<GalleryImage>>(emptyList())
        private set

    var dbImages by mutableStateOf<List<MediaEntity>>(emptyList())
        private set

    init {
        observeDatabase()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            mediaRepository.getAllMedia().collectLatest { list ->
                dbImages = list
            }
        }
    }

    fun scanImages() {
        viewModelScope.launch {
            val loaded = scanner.loadImages()
            images = loaded
            syncToDatabase(loaded)
        }
    }

    private fun syncToDatabase(list: List<GalleryImage>) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = list.map {
                MediaEntity(
                    id = it.id,
                    uri = it.uri,
                    bucket = it.bucketName,
                    dateTaken = it.dateTaken,
                    size = it.size
                )
            }
            mediaRepository.clear()
            mediaRepository.insertAll(entities)
        }
    }
}

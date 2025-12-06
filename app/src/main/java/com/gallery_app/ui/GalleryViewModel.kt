package com.gallery_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.MediaScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val scanner: MediaScanner
) : ViewModel() {

    var imageCount by mutableStateOf(0)
        private set

    fun scanImages() {
        viewModelScope.launch {
            val images = scanner.loadImages()
            imageCount = images.size
        }
    }
}

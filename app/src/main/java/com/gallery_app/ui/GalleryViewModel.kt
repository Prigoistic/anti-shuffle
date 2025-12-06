package com.gallery_app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery_app.data.GalleryImage
import com.gallery_app.data.MediaScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val scanner: MediaScanner
) : ViewModel() {

    // public observable list of images for the UI
    var images by mutableStateOf<List<GalleryImage>>(emptyList())
        private set

    // optional: keep count derived from images for backward compatibility
    val imageCount: Int
        get() = images.size

    fun scanImages() {
        viewModelScope.launch {
            try {
                val loaded = scanner.loadImages()
                images = loaded
            } catch (t: Throwable) {
                // handle/log error if needed
                images = emptyList()
            }
        }
    }
}

package com.gallery_app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GalleryHomeScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.scanImages()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Total Images : ${viewModel.imageCount}")
    }
}

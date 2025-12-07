package com.gallery_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gallery_app.data.GalleryImage

@Composable
fun GalleryHomeScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.scanImages()
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is GalleryUiState.Loading -> LoadingScreen()
        is GalleryUiState.Empty -> EmptyScreen()
        is GalleryUiState.Error -> ErrorScreen(state.message)
        is GalleryUiState.Success -> ImageGrid(state.images)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading images…", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("No images found", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("Error: $message", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ImageGrid(images: List<GalleryImage>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(images) { img ->
            ThumbnailItem(image = img)
        }
    }
}

@Composable
fun ThumbnailItem(
    image: GalleryImage,
    onClick: (GalleryImage) -> Unit = {}
) {
    AsyncImage(
        model = image.uri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick(image) }
    )
}

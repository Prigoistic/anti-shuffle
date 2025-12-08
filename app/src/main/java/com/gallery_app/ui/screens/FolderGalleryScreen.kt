package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gallery_app.ui.GalleryUiState
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderGalleryScreen(
    bucket: String,
    viewModel: GalleryViewModel,
    onImageClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredImages = when (val s = uiState) {
        is GalleryUiState.Success -> s.images.filter { it.bucketName == bucket }
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite)
    ) {
        BrutalTopAppBar(
            title = bucket.uppercase(),
            backgroundColor = BrutalColors.Orange,
            navigationIcon = {
                BrutalIconButton(
                    onClick = onBack,
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    backgroundColor = BrutalColors.White
                )
            }
        )

        when {
            uiState is GalleryUiState.Loading -> {
                BrutalLoadingBox()
            }
            filteredImages.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BrutalCard(
                        modifier = Modifier.padding(24.dp),
                        backgroundColor = BrutalColors.Yellow
                    ) {
                        BrutalHeadline(
                            text = "NO IMAGES HERE",
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BrutalColors.OffWhite),
                    columns = GridCells.Adaptive(140.dp),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredImages, key = { it.id }) { item ->
                        BrutalImageContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.uri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onImageClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

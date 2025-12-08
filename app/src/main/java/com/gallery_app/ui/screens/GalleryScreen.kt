package com.gallery_app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gallery_app.data.GalleryImage
import com.gallery_app.ui.GalleryUiState
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onImageClick: (Long) -> Unit,
    onOpenFolders: () -> Unit
) {
    // Trigger initial scan once when this screen first composes.
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!started) {
            started = true
            viewModel.scanImages()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite)
    ) {
        BrutalTopAppBar(
            title = "GALLERY",
            backgroundColor = BrutalColors.Cyan,
            actions = {
                BrutalIconButton(
                    onClick = { viewModel.scanImages() },
                    icon = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    backgroundColor = BrutalColors.Yellow
                )
                Spacer(modifier = Modifier.width(8.dp))
                BrutalIconButton(
                    onClick = onOpenFolders,
                    icon = Icons.Filled.FolderOpen,
                    contentDescription = "Folders",
                    backgroundColor = BrutalColors.Pink
                )
            }
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is GalleryUiState.Loading -> LoadingScreen()
                is GalleryUiState.Empty -> EmptyScreen(onOpenFolders, onRefresh = { viewModel.scanImages() })
                is GalleryUiState.Error -> ErrorScreen(state.message)
                is GalleryUiState.Success -> {
                    GalleryGrid(
                        items = state.images,
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    BrutalLoadingBox()
}

@Composable
private fun EmptyScreen(onOpenFolders: () -> Unit = {}, onRefresh: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            BrutalCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 24.dp),
                backgroundColor = BrutalColors.Yellow
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BrutalTitle("NO IMAGES")
                    Spacer(Modifier.height(12.dp))
                    BrutalBody(
                        text = "Scan your device to find images",
                        color = BrutalColors.Black.copy(alpha = 0.7f)
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                BrutalButton(
                    onClick = onRefresh,
                    backgroundColor = BrutalColors.Lime
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = BrutalColors.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "SCAN NOW",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                    )
                }
                BrutalOutlinedButton(onClick = onOpenFolders) {
                    Icon(
                        Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = BrutalColors.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "FOLDERS",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                    )
                }
            }
        }
    }
}
@Composable
private fun ErrorScreen(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite),
        contentAlignment = Alignment.Center
    ) {
        BrutalCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            backgroundColor = BrutalColors.Red
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                BrutalTitle(
                    text = "ERROR!",
                    color = BrutalColors.White
                )
                Spacer(Modifier.height(16.dp))
                BrutalBody(
                    text = msg,
                    color = BrutalColors.White
                )
            }
        }
    }
}

@Composable
private fun GalleryGrid(
    items: List<GalleryImage>,
    onImageClick: (Long) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite),
        columns = GridCells.Adaptive(140.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = { it.id }) { item ->
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



package com.gallery_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.gallery_app.data.GalleryImage
import com.gallery_app.image.ThumbnailLoader
import com.gallery_app.ui.GalleryUiState
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onImageClick: (Long) -> Unit,
    onOpenFolders: () -> Unit
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!started) {
            started = true
            viewModel.scanImages()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val pagedImages = viewModel.pagedMedia.collectAsLazyPagingItems()
    
    // Get current date for display
    val currentDate = remember {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val dayNum = calendar.get(Calendar.DAY_OF_MONTH)
        val suffix = when {
            dayNum in 11..13 -> "th"
            dayNum % 10 == 1 -> "st"
            dayNum % 10 == 2 -> "nd"
            dayNum % 10 == 3 -> "rd"
            else -> "th"
        }
        Pair(dayFormat.format(calendar.time), "$dayNum$suffix")
    }

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            GlassTopBar(
                navigationIcon = {
                    GlassIconButton(
                        onClick = { viewModel.scanImages() },
                        icon = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                },
                actions = {
                    GlassMenuButton(onClick = onOpenFolders)
                }
            )

            when (val state = uiState) {
                is GalleryUiState.Loading -> {
                    GlassLoadingIndicator()
                }
                is GalleryUiState.Empty -> {
                    EmptyScreen(
                        onOpenFolders = onOpenFolders,
                        onRefresh = { viewModel.scanImages() }
                    )
                }
                is GalleryUiState.Error -> {
                    ErrorScreen(state.message)
                }
                is GalleryUiState.Success -> {
                    // Header with date
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = currentDate.first,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassColors.TextPrimary
                        )
                        Text(
                            text = currentDate.second,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Light,
                            color = GlassColors.TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Gallery Grid
                    PagedGalleryGrid(
                        pagingItems = pagedImages,
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyScreen(onOpenFolders: () -> Unit, onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassHeadline(
                        text = "No Photos Yet",
                        color = GlassColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassBodyText(
                        text = "Scan your device to discover photos"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassIconButton(
                    onClick = onRefresh,
                    icon = Icons.Filled.Refresh,
                    contentDescription = "Scan",
                    size = 56.dp,
                    backgroundColor = GlassColors.AccentBlue.copy(alpha = 0.3f)
                )
                GlassIconButton(
                    onClick = onOpenFolders,
                    icon = Icons.Filled.FolderOpen,
                    contentDescription = "Folders",
                    size = 56.dp,
                    backgroundColor = GlassColors.AccentPurple.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(msg: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            backgroundColor = GlassColors.Error.copy(alpha = 0.2f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                GlassHeadline(
                    text = "Error",
                    color = GlassColors.Error
                )
                Spacer(modifier = Modifier.height(16.dp))
                GlassBodyText(
                    text = msg,
                    color = GlassColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun PagedGalleryGrid(
    pagingItems: LazyPagingItems<GalleryImage>,
    onImageClick: (Long) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index -> pagingItems.peek(index)?.id ?: index }
        ) { index ->
            val item = pagingItems[index]
            val context = LocalContext.current
            if (item != null) {
                GlassImageCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    onClick = { onImageClick(item.id) }
                ) {
                    AsyncImage(
                        model = ThumbnailLoader.createThumbnailRequest(
                            context = context,
                            mediaId = item.id,
                            targetSize = 400
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            } else {
                GlassShimmer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
        
        // Loading state at the bottom
        when (pagingItems.loadState.append) {
            is LoadState.Loading -> {
                items(6) {
                    GlassShimmer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
            else -> {}
        }
    }
}



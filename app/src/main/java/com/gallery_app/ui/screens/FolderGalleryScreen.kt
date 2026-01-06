package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gallery_app.image.ThumbnailLoader
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
    // Get paged images for this specific bucket
    val pagedImages = remember(bucket) {
        viewModel.getPagedMediaByBucket(bucket)
    }.collectAsLazyPagingItems()

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
            pagedImages.loadState.refresh is LoadState.Loading -> {
                BrutalLoadingBox()
            }
            pagedImages.itemCount == 0 && pagedImages.loadState.refresh is LoadState.NotLoading -> {
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
                    items(
                        count = pagedImages.itemCount,
                        key = { index -> pagedImages.peek(index)?.id ?: index }
                    ) { index ->
                        val item = pagedImages[index]
                        val context = LocalContext.current
                        if (item != null) {
                            BrutalImageContainer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
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
                                        .clickable { onImageClick(item.id) }
                                )
                            }
                        } else {
                            // Shimmer placeholder
                            ShimmerPlaceholder()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerPlaceholder() {
    BrutalImageContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrutalColors.OffWhite)
        )
    }
}

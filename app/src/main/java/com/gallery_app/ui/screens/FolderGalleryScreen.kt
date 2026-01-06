package com.gallery_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.gallery_app.image.ThumbnailLoader
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*

@Composable
fun FolderGalleryScreen(
    bucket: String,
    viewModel: GalleryViewModel,
    onImageClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val pagedImages = remember(bucket) {
        viewModel.getPagedMediaByBucket(bucket)
    }.collectAsLazyPagingItems()

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
                        onClick = onBack,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                },
                actions = {
                    GlassMenuButton(onClick = { })
                }
            )

            // Title Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    text = bucket,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary
                )
                Text(
                    text = "${pagedImages.itemCount} photos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlassColors.TextSecondary.copy(alpha = 0.7f)
                )
            }

            when {
                pagedImages.loadState.refresh is LoadState.Loading -> {
                    GlassLoadingIndicator()
                }
                pagedImages.itemCount == 0 && pagedImages.loadState.refresh is LoadState.NotLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                GlassHeadline(
                                    text = "No Photos",
                                    color = GlassColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GlassBodyText(
                                    text = "This album is empty"
                                )
                            }
                        }
                    }
                }
                else -> {
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
                            count = pagedImages.itemCount,
                            key = { index -> pagedImages.peek(index)?.id ?: index }
                        ) { index ->
                            val item = pagedImages[index]
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
                        
                        // Loading more indicator
                        when (pagedImages.loadState.append) {
                            is LoadState.Loading -> {
                                items(3) {
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
            }
        }
    }
}

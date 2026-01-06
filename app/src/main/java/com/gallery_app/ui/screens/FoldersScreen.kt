package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gallery_app.data.db.BucketInfo
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*

@Composable
fun FoldersScreen(
    viewModel: GalleryViewModel,
    onBucketClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val buckets by viewModel.buckets.collectAsState()

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
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Your",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary
                )
                Text(
                    text = "Albums",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = GlassColors.TextSecondary.copy(alpha = 0.7f)
                )
            }

            if (buckets.isEmpty()) {
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
                                text = "No Albums Yet",
                                color = GlassColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            GlassBodyText(
                                text = "Your photo albums will appear here"
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                ) {
                    items(buckets, key = { it.bucket }) { bucket ->
                        FolderCard(
                            bucket = bucket,
                            onClick = { onBucketClick(bucket.bucket) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderCard(bucket: BucketInfo, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        cornerRadius = 20.dp,
        backgroundColor = GlassColors.FolderDark.copy(alpha = 0.85f),
        borderColor = GlassColors.GlassBorder.copy(alpha = 0.15f),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cover image as subtle background
            if (bucket.coverUri != null) {
                AsyncImage(
                    model = bucket.coverUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    alpha = 0.15f
                )
            }
            
            // Folder tab decoration at top
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 12.dp)
                    .width(40.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                    .background(GlassColors.GlassBorder.copy(alpha = 0.4f))
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Folder thumbnail
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassColors.GlassBorder.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (bucket.coverUri != null) {
                        AsyncImage(
                            model = bucket.coverUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
                
                // Folder info
                Column {
                    Text(
                        text = bucket.bucket,
                        color = GlassColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${bucket.count} items",
                        color = GlassColors.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}


package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
            // Premium Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassColors.GlassDark.copy(alpha = 0.5f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = GlassColors.TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                // Title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Collections,
                        contentDescription = null,
                        tint = GlassColors.AccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Albums",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GlassColors.TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
                
                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(44.dp))
            }

            // Title Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Your",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Collections",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    color = GlassColors.TextSecondary.copy(alpha = 0.7f),
                    letterSpacing = (-1).sp
                )
                
                // Album count
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${buckets.size} albums",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlassColors.TextMuted
                )
            }

            if (buckets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(GlassColors.AccentPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Folder,
                                contentDescription = null,
                                tint = GlassColors.AccentPurple,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No Albums Yet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your photo albums will appear here",
                            fontSize = 14.sp,
                            color = GlassColors.TextMuted
                        )
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
            // Cover image as background
            if (bucket.coverUri != null) {
                AsyncImage(
                    model = bucket.coverUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    alpha = 0.2f
                )
            }
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GlassColors.FolderDark.copy(alpha = 0.3f),
                                GlassColors.FolderDark.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
            
            // Folder tab decoration at top
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 12.dp)
                    .width(36.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(GlassColors.GlassBorder.copy(alpha = 0.5f))
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
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = GlassColors.TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Folder info
                Column {
                    Text(
                        text = bucket.bucket,
                        color = GlassColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null,
                            tint = GlassColors.TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${bucket.count} photos",
                            color = GlassColors.TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


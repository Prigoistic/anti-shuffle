package com.gallery_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.util.concurrent.TimeUnit

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

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Premium Top Bar
            PremiumTopBar(
                onRefresh = { viewModel.scanImages() },
                onOpenFolders = onOpenFolders
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
                    DateGroupedGallery(
                        pagingItems = pagedImages,
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumTopBar(
    onRefresh: () -> Unit,
    onOpenFolders: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sync button with glow
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GlassColors.GlassDark.copy(alpha = 0.5f))
                .clickable(onClick = onRefresh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudSync,
                contentDescription = "Sync",
                tint = GlassColors.AccentCyan,
                modifier = Modifier.size(22.dp)
            )
        }
        
        // Title with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = GlassColors.AccentPurple,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Gallery",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = GlassColors.TextPrimary,
                letterSpacing = 0.5.sp
            )
        }
        
        // Folders button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GlassColors.GlassDark.copy(alpha = 0.5f))
                .clickable(onClick = onOpenFolders),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = "Folders",
                tint = GlassColors.TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun DateGroupedGallery(
    pagingItems: LazyPagingItems<GalleryImage>,
    onImageClick: (Long) -> Unit
) {
    val context = LocalContext.current
    
    if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.Loading) {
        GlassLoadingIndicator()
        return
    }
    
    // Pre-calculate header positions for displayed items
    val headerPositions = remember(pagingItems.itemSnapshotList) {
        val positions = mutableMapOf<Int, Pair<String, Int>>() // index -> (label, count)
        var lastLabel: String? = null
        
        pagingItems.itemSnapshotList.forEachIndexed { index, image ->
            if (image != null) {
                val label = getDateLabel(image.dateTaken)
                if (label != lastLabel) {
                    // Count how many consecutive items have this label
                    var count = 1
                    for (i in (index + 1) until pagingItems.itemSnapshotList.size) {
                        val nextImage = pagingItems.itemSnapshotList[i]
                        if (nextImage != null && getDateLabel(nextImage.dateTaken) == label) {
                            count++
                        } else {
                            break
                        }
                    }
                    positions[index] = Pair(label, count)
                    lastLabel = label
                }
            }
        }
        positions
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Iterate through all items
        for (index in 0 until pagingItems.itemCount) {
            // Check if we need a header before this item
            headerPositions[index]?.let { (label, count) ->
                item(
                    key = "header_${label}_$index",
                    span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }
                ) {
                    DateHeader(dateLabel = label, count = count)
                }
            }
            
            // Add image item - accessing pagingItems[index] triggers loading
            item(key = "image_$index") {
                val image = pagingItems[index]
                if (image != null) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onImageClick(image.id) }
                    ) {
                        AsyncImage(
                            model = ThumbnailLoader.createThumbnailRequest(
                                context = context,
                                mediaId = image.id,
                                targetSize = 400
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Placeholder while loading
                    GlassShimmer(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
        
        // Loading indicator at bottom
        if (pagingItems.loadState.append is LoadState.Loading) {
            item(
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GlassColors.AccentBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        // Error handling for append
        if (pagingItems.loadState.append is LoadState.Error) {
            item(
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading more images",
                        color = GlassColors.Error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(dateLabel: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Calendar icon with glass background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GlassColors.AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = GlassColors.AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = dateLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary,
                    letterSpacing = (-0.3).sp
                )
            }
        }
        
        // Photo count badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GlassColors.GlassDark.copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = GlassColors.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "$count",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlassColors.TextSecondary
                )
            }
        }
    }
}

private fun getDateLabel(timestamp: Long): String {
    if (timestamp <= 0) return "Unknown"
    
    val now = Calendar.getInstance()
    val imageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    
    val daysDiff = TimeUnit.MILLISECONDS.toDays(now.timeInMillis - timestamp)
    
    return when {
        isSameDay(now, imageDate) -> "Today"
        daysDiff == 1L -> "Yesterday"
        daysDiff < 7 -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
        }
        isSameYear(now, imageDate) -> {
            SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(timestamp))
        }
        else -> {
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
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
            // Premium empty state icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                GlassColors.AccentBlue.copy(alpha = 0.2f),
                                GlassColors.AccentPurple.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = GlassColors.AccentBlue,
                    modifier = Modifier.size(56.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "No Memories Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GlassColors.TextPrimary,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Scan your device to discover your photos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = GlassColors.TextMuted
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Scan button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    GlassColors.AccentBlue,
                                    GlassColors.AccentPurple
                                )
                            )
                        )
                        .clickable(onClick = onRefresh)
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Scan Now",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                
                // Folders button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassColors.GlassDark.copy(alpha = 0.6f))
                        .clickable(onClick = onOpenFolders)
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = GlassColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Albums",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GlassColors.TextPrimary
                        )
                    }
                }
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
            backgroundColor = GlassColors.Error.copy(alpha = 0.15f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Something went wrong",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassColors.Error
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlassColors.TextSecondary
                )
            }
        }
    }
}



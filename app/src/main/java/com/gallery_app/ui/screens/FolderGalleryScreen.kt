package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.rounded.Folder
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
import com.gallery_app.data.GalleryImage
import com.gallery_app.image.ThumbnailLoader
import com.gallery_app.ui.GalleryViewModel
import com.gallery_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = GlassColors.AccentBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Album",
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
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    text = bucket,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
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
                        text = "${pagedImages.itemCount} photos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlassColors.TextMuted
                    )
                }
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(GlassColors.AccentBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Folder,
                                    contentDescription = null,
                                    tint = GlassColors.AccentBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "No Photos",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This album is empty",
                                fontSize = 14.sp,
                                color = GlassColors.TextMuted
                            )
                        }
                    }
                }
                else -> {
                    // Group images by date
                    val groupedImages = remember(pagedImages.itemCount) {
                        val images = (0 until pagedImages.itemCount).mapNotNull { pagedImages.peek(it) }
                        images.groupBy { getDateLabel(it.dateTaken) }
                            .toList()
                            .sortedByDescending { (_, imgs) -> imgs.firstOrNull()?.dateTaken ?: 0L }
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        groupedImages.forEach { (dateLabel, images) ->
                            // Date header
                            item(key = "header_${bucket}_$dateLabel") {
                                DateHeader(dateLabel = dateLabel, count = images.size)
                            }
                            
                            // Images grid for this date
                            item(key = "grid_${bucket}_$dateLabel") {
                                DateImageGrid(
                                    images = images,
                                    onImageClick = onImageClick
                                )
                            }
                        }
                        
                        // Loading indicator
                        if (pagedImages.loadState.append is LoadState.Loading) {
                            item {
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
                    }
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GlassColors.AccentPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = GlassColors.AccentPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = dateLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = GlassColors.TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }
        
        Text(
            text = "$count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = GlassColors.TextMuted
        )
    }
}

@Composable
private fun DateImageGrid(
    images: List<GalleryImage>,
    onImageClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val rows = images.chunked(3)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        rows.forEach { rowImages ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowImages.forEach { image ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
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
                }
                
                repeat(3 - rowImages.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
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
        daysDiff < 7 -> SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
        isSameYear(now, imageDate) -> SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}

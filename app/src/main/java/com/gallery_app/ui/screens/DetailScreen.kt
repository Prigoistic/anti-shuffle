package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gallery_app.ui.DetailViewModel
import com.gallery_app.ui.DetailUiState
import com.gallery_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailScreen(
    id: Long,
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(id) {
        viewModel.loadMedia(id)
    }

    val mediaState by viewModel.mediaState.collectAsState()
    var showInfo by remember { mutableStateOf(false) }

    GlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            when (val state = mediaState) {
                is DetailUiState.Loading -> {
                    GlassLoadingIndicator()
                }
                is DetailUiState.Success -> {
                    val media = state.media
                    
                    // Full screen image
                    AsyncImage(
                        model = media.uri,
                        contentDescription = "Full size image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Top gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GlassColors.DarkBlueStart.copy(alpha = 0.8f),
                                        GlassColors.DarkBlueStart.copy(alpha = 0f)
                                    )
                                )
                            )
                    )
                    
                    // Top Bar
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
                        
                        // Info button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (showInfo) GlassColors.AccentBlue.copy(alpha = 0.4f) 
                                    else GlassColors.GlassDark.copy(alpha = 0.5f)
                                )
                                .clickable(onClick = { showInfo = !showInfo }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = if (showInfo) GlassColors.AccentCyan else GlassColors.TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    
                    // Bottom gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GlassColors.DarkBlueStart.copy(alpha = 0f),
                                        GlassColors.DarkBlueStart.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )
                    
                    // Info panel
                    if (showInfo) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .navigationBarsPadding()
                        ) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                backgroundColor = GlassColors.GlassDark.copy(alpha = 0.9f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    Text(
                                        text = "Photo Details",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassColors.TextPrimary,
                                        letterSpacing = (-0.3).sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    MetadataRow(
                                        icon = Icons.Outlined.CalendarMonth,
                                        label = "Date",
                                        value = formatDate(media.dateTaken)
                                    )
                                    MetadataRow(
                                        icon = Icons.Outlined.Folder,
                                        label = "Album",
                                        value = media.bucketName
                                    )
                                    MetadataRow(
                                        icon = Icons.Outlined.Storage,
                                        label = "Size",
                                        value = formatBytes(media.size)
                                    )
                                }
                            }
                        }
                    }
                }
                is DetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Something went wrong",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GlassColors.Error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.message,
                                fontSize = 14.sp,
                                color = GlassColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassColors.GlassDark.copy(alpha = 0.6f))
                                    .clickable(onClick = onBack)
                                    .padding(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    text = "Go Back",
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
    }
}

@Composable
private fun MetadataRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GlassColors.GlassBorder.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassColors.AccentBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = GlassColors.TextMuted
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = GlassColors.TextPrimary
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return if (timestamp > 0) {
        SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(timestamp))
    } else {
        "Unknown"
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
    }
}

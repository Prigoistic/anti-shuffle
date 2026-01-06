package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                    
                    // Top gradient overlay for better visibility
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GlassColors.DarkBlueStart.copy(alpha = 0.7f),
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
                        GlassIconButton(
                            onClick = onBack,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                        
                        GlassIconButton(
                            onClick = { showInfo = !showInfo },
                            icon = Icons.Default.Info,
                            contentDescription = "Info",
                            backgroundColor = if (showInfo) 
                                GlassColors.AccentBlue.copy(alpha = 0.5f) 
                            else 
                                GlassColors.GlassDark.copy(alpha = 0.6f)
                        )
                    }
                    
                    // Bottom gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GlassColors.DarkBlueStart.copy(alpha = 0f),
                                        GlassColors.DarkBlueStart.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    
                    // Info panel (when showInfo is true)
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
                                cornerRadius = 20.dp,
                                backgroundColor = GlassColors.GlassDark.copy(alpha = 0.85f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "Details",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassColors.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    MetadataRow("Date", formatDate(media.dateTaken))
                                    MetadataRow("Album", media.bucketName)
                                    MetadataRow("Size", formatBytes(media.size))
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
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = GlassColors.Error.copy(alpha = 0.2f)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                GlassHeadline(
                                    text = "Error",
                                    color = GlassColors.Error
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                GlassBodyText(
                                    text = state.message,
                                    color = GlassColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                GlassIconButton(
                                    onClick = onBack,
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back",
                                    size = 56.dp,
                                    backgroundColor = GlassColors.AccentBlue.copy(alpha = 0.3f)
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
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = GlassColors.TextMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = GlassColors.TextPrimary
        )
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

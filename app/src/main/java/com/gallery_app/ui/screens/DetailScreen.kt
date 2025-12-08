package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize().background(BrutalColors.Black)) {
        when (val state = mediaState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BrutalCard(
                        modifier = Modifier.size(120.dp),
                        backgroundColor = BrutalColors.Cyan
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = BrutalColors.Black,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                }
            }
            is DetailUiState.Success -> {
                val media = state.media
                Column(modifier = Modifier.fillMaxSize()) {
                    // Brutal Top Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        BrutalIconButton(
                            onClick = onBack,
                            icon = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            backgroundColor = BrutalColors.Yellow
                        )
                    }

                    // Fullscreen Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = media.uri,
                            contentDescription = "Full size image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Brutal Metadata Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        BrutalCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = BrutalColors.Lime
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                BrutalHeadline("DETAILS")
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                MetadataRow("DATE", formatDate(media.dateTaken))
                                MetadataRow("FOLDER", media.bucketName)
                                MetadataRow("SIZE", formatBytes(media.size))
                                MetadataRow("ID", media.id.toString())
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
                    BrutalCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = BrutalColors.Red
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BrutalTitle(
                                text = "ERROR!",
                                color = BrutalColors.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            BrutalBody(
                                text = state.message,
                                color = BrutalColors.White
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            BrutalButton(
                                onClick = onBack,
                                backgroundColor = BrutalColors.Yellow
                            ) {
                                Text(
                                    "GO BACK",
                                    fontWeight = FontWeight.Black
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
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = BrutalColors.Black,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BrutalColors.Black.copy(alpha = 0.8f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return if (timestamp > 0) {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
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

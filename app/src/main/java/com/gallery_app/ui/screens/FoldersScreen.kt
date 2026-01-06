package com.gallery_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gallery_app.data.db.BucketInfo
import com.gallery_app.ui.GalleryViewModel
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.gallery_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    viewModel: GalleryViewModel,
    onBucketClick: (String) -> Unit,
    onBack: () -> Unit
) {
    // Use the new buckets StateFlow directly from ViewModel
    val buckets by viewModel.buckets.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite)
    ) {
        BrutalTopAppBar(
            title = "FOLDERS",
            backgroundColor = BrutalColors.Pink,
            navigationIcon = {
                BrutalIconButton(
                    onClick = onBack,
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    backgroundColor = BrutalColors.White
                )
            }
        )

        if (buckets.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BrutalCard(
                    modifier = Modifier.padding(24.dp),
                    backgroundColor = BrutalColors.Yellow
                ) {
                    BrutalHeadline(
                        text = "NO FOLDERS",
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrutalColors.OffWhite)
            ) {
                items(buckets, key = { it.bucket }) { bucket ->
                    BucketCard(bucket = bucket, onClick = { onBucketClick(bucket.bucket) })
                }
            }
        }
    }
}

@Composable
private fun BucketCard(bucket: BucketInfo, onClick: () -> Unit) {
    val colors = listOf(
        BrutalColors.Yellow,
        BrutalColors.Cyan,
        BrutalColors.Pink,
        BrutalColors.Lime
    )
    val cardColor = colors[bucket.bucket.hashCode().mod(colors.size)]
    
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = BrutalColors.White,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BrutalImageContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
            ) {
                AsyncImage(
                    model = bucket.coverUri,
                    contentDescription = bucket.bucket,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = bucket.bucket.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = BrutalColors.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${bucket.count} ITEMS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrutalColors.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


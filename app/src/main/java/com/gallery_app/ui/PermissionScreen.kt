package com.gallery_app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.gallery_app.ui.theme.*

@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val isGranted = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onPermissionGranted()
    }

    LaunchedEffect(Unit) {
        if (!isGranted) {
            launcher.launch(permission)
        } else {
            onPermissionGranted()
        }
    }

    GlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                // Icon
                GlassCard(
                    modifier = Modifier.size(100.dp),
                    cornerRadius = 50.dp,
                    backgroundColor = GlassColors.AccentBlue.copy(alpha = 0.2f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = GlassColors.AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Gallery Access",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Please grant permission to access your photos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlassColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                GlassLoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

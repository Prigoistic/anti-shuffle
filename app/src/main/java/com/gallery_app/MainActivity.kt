package com.gallery_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.gallery_app.ui.GalleryHomeScreen
import com.gallery_app.ui.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var permissionGranted by remember { mutableStateOf(false) }

                if (!permissionGranted) {
                    PermissionScreen {
                        permissionGranted = true
                    }
                } else {
                    GalleryHomeScreen()
                }
            }
        }
    }
}

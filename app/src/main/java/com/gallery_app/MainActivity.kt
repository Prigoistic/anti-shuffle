package com.gallery_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gallery_app.ui.PermissionScreen
import com.gallery_app.ui.GalleryHomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var permissionGranted by remember { mutableStateOf(false) }
                if(!permissionGranted){
                    PermissionScreen{
                        permissionGranted = true
                    }
                }else{
                    GalleryHomeScreen()
                }

            }
        }
    }
}

package com.gallery_app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gallery_app.ui.PermissionScreen
import com.gallery_app.ui.screens.DetailScreen
import com.gallery_app.ui.screens.FolderGalleryScreen
import com.gallery_app.ui.screens.FoldersScreen
import com.gallery_app.ui.screens.GalleryScreen
import com.gallery_app.ui.theme.GlassColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )
        
        // Make content extend behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = GlassColors.DarkBlueStart,
                    surface = GlassColors.GlassDark,
                    primary = GlassColors.AccentBlue,
                    onBackground = GlassColors.TextPrimary,
                    onSurface = GlassColors.TextPrimary
                )
            ) {
                var permissionGranted by remember { mutableStateOf(false) }

                if (!permissionGranted) {
                    PermissionScreen {
                        permissionGranted = true
                    }
                } else {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "gallery") {
                        composable("gallery") {
                            GalleryScreen(
                                viewModel = hiltViewModel(),
                                onImageClick = { id ->
                                    navController.navigate("detail/$id")
                                },
                                onOpenFolders = {
                                    navController.navigate("folders")
                                }
                            )
                        }

                        composable("folders") {
                            FoldersScreen(
                                viewModel = hiltViewModel(),
                                onBucketClick = { bucket ->
                                    navController.navigate("folder/${bucket}")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "folder/{bucket}",
                            arguments = listOf(navArgument("bucket") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val bucket = backStackEntry.arguments?.getString("bucket").orEmpty()
                            FolderGalleryScreen(
                                bucket = bucket,
                                viewModel = hiltViewModel(),
                                onImageClick = { id -> navController.navigate("detail/$id") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "detail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: 0L
                            DetailScreen(
                                id = id,
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

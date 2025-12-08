package com.gallery_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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

# Gallery App - Navigation & Architecture Reference

## 🗺️ Navigation Graph

```
┌─────────────────────────────────────────────────────────────┐
│                      Permission Screen                       │
│                  (PermissionScreen.kt)                       │
│                                                              │
│              ✅ Grant Permission → NavHost                   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Route: "gallery"                          │
│                     GalleryScreen                            │
│  ┌────────────────────────────────────────────────────┐    │
│  │  🖼️ LazyVerticalGrid - All Images                 │    │
│  │  • Adaptive columns (128.dp)                       │    │
│  │  • Loads from Room via Flow                        │    │
│  │  • Real-time updates                               │    │
│  │  • TopAppBar: Refresh + Folders buttons           │    │
│  └────────────────────────────────────────────────────┘    │
│         │                                │                   │
│         │ onImageClick(id)              │ onOpenFolders     │
│         ↓                                ↓                   │
└─────────────────────────────────────────────────────────────┘
         │                                │
         │                                │
         │                    ┌───────────┘
         │                    │
         │                    ↓
         │    ┌─────────────────────────────────────────────┐
         │    │           Route: "folders"                  │
         │    │           FoldersScreen                     │
         │    │  ┌──────────────────────────────────────┐  │
         │    │  │  📁 Bucket Grid                      │  │
         │    │  │  • Groups images by folder           │  │
         │    │  │  • Shows cover + count               │  │
         │    │  │  • Sorted by size                    │  │
         │    │  └──────────────────────────────────────┘  │
         │    │               │                             │
         │    │               │ onBucketClick(bucket)       │
         │    │               ↓                             │
         │    └─────────────────────────────────────────────┘
         │                    │
         │                    ↓
         │    ┌─────────────────────────────────────────────┐
         │    │       Route: "folder/{bucket}"              │
         │    │       FolderGalleryScreen                   │
         │    │  ┌──────────────────────────────────────┐  │
         │    │  │  🖼️ Filtered Grid                    │  │
         │    │  │  • Shows only images from bucket     │  │
         │    │  │  • Same layout as main gallery       │  │
         │    │  └──────────────────────────────────────┘  │
         │    │               │                             │
         │    │               │ onImageClick(id)            │
         │    └───────────────┼─────────────────────────────┘
         │                    │
         └────────────────────┘
                              ↓
         ┌─────────────────────────────────────────────────┐
         │          Route: "detail/{id}"                   │
         │          DetailScreen                           │
         │  ┌──────────────────────────────────────────┐  │
         │  │  🖼️ Fullscreen Image                     │  │
         │  │  • Black immersive background             │  │
         │  │  • ContentScale.Fit                       │  │
         │  │  • Metadata panel:                        │  │
         │  │    - Date taken                           │  │
         │  │    - Folder name                          │  │
         │  │    - File size                            │  │
         │  │    - Media ID                             │  │
         │  └──────────────────────────────────────────┘  │
         │               │ onBack()                        │
         │               ↓                                 │
         └─────────────────────────────────────────────────┘
```

## 📊 Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
├─────────────────────────────────────────────────────────────┤
│  GalleryScreen  │  FoldersScreen  │  FolderGalleryScreen    │
│  DetailScreen                                                │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ↓ collectAsState()
┌─────────────────────────────────────────────────────────────┐
│                      VIEWMODEL LAYER                         │
├─────────────────────────────────────────────────────────────┤
│  GalleryViewModel                 DetailViewModel            │
│  • StateFlow<GalleryUiState>     • StateFlow<DetailUiState> │
│  • scanImages()                   • loadMedia(id)            │
│  • observeDatabase()                                         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ↓ Flow<List<MediaEntity>>
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                          │
├─────────────────────────────────────────────────────────────┤
│  MediaRepository (Interface)                                 │
│  MediaRepositoryImpl                                         │
│  • getAllMedia(): Flow<List<MediaEntity>>                   │
│  • getById(id): Flow<MediaEntity?>                          │
│  • getByBucket(name): Flow<List<MediaEntity>>               │
│  • insertAll(list)                                           │
│  • clear()                                                   │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ↓ DAO methods
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                         │
├─────────────────────────────────────────────────────────────┤
│  MediaDao (Room DAO)                                         │
│  • getAll(): Flow<List<MediaEntity>>                        │
│  • getById(id): Flow<MediaEntity?>                          │
│  • getByBucket(name): Flow<List<MediaEntity>>               │
│  • insertAll(media: List<MediaEntity>)                      │
│  • clear()                                                   │
├─────────────────────────────────────────────────────────────┤
│  MediaEntity (@Entity)                                       │
│  • id: Long                                                  │
│  • uri: String                                               │
│  • dateTaken: Long                                           │
│  • bucket: String                                            │
│  • size: Long                                                │
└─────────────────────────────────────────────────────────────┘
                 ↑
                 │ scan & insert
┌─────────────────────────────────────────────────────────────┐
│                        DATA SOURCE                           │
├─────────────────────────────────────────────────────────────┤
│  MediaScanner                                                │
│  • loadImages(): List<GalleryImage>                         │
│  • Queries MediaStore.Images                                │
│  • Returns GalleryImage (domain model)                      │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 State Management

### GalleryUiState
```kotlin
sealed interface GalleryUiState {
    object Loading : GalleryUiState
    data class Success(val images: List<GalleryImage>) : GalleryUiState
    object Empty : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}
```

### DetailUiState
```kotlin
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val media: GalleryImage) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
```

## 🔄 Key Flows

### 1. Initial App Launch
```
User launches app
  → PermissionScreen (if not granted)
  → User grants permission
  → Navigate to GalleryScreen
  → LaunchedEffect triggers scanImages()
  → MediaScanner queries MediaStore
  → Results inserted into Room
  → Flow emits to ViewModel
  → UI updates with images
```

### 2. View Image Details
```
User on GalleryScreen
  → Taps image thumbnail
  → onImageClick(id) triggered
  → navController.navigate("detail/$id")
  → DetailScreen loads
  → DetailViewModel.loadMedia(id)
  → Repository queries Room by ID
  → Flow emits MediaEntity
  → Mapped to GalleryImage
  → UI shows fullscreen + metadata
```

### 3. Browse Folders
```
User on GalleryScreen
  → Taps Folders icon in TopAppBar
  → Navigate to FoldersScreen
  → ViewModel state collected
  → Images grouped by bucketName
  → User taps folder
  → Navigate to FolderGalleryScreen with bucket name
  → Images filtered by bucket
  → User taps image → Navigate to DetailScreen
```

### 4. Real-time Database Updates
```
Background scan triggered
  → New images inserted to Room
  → Flow automatically emits new data
  → ViewModel receives update
  → StateFlow value changes
  → All observing Composables recompose
  → UI updates instantly
```

## 🏗️ Dependency Injection (Hilt)

```
@HiltAndroidApp
App

@AndroidEntryPoint
MainActivity

@HiltViewModel
GalleryViewModel(@Inject constructor(scanner, repository))

@HiltViewModel
DetailViewModel(@Inject constructor(repository))

@Module
DatabaseModule
  → provides MediaDatabase
  → provides MediaDao

@Module
RepositoryModule
  → provides MediaRepository

@Module
ScannerModule
  → provides MediaScanner
```

## 📦 Key Dependencies

```gradle
// Compose BOM
androidx.compose:compose-bom:2024.05.00

// Compose UI
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.lifecycle:lifecycle-viewmodel-compose

// Navigation
androidx.navigation:navigation-compose
androidx.hilt:hilt-navigation-compose

// Image Loading
io.coil-kt:coil-compose:2.4.0

// Database
androidx.room:room-runtime
androidx.room:room-ktx

// DI
com.google.dagger:hilt-android
```

## ✨ Best Practices Implemented

1. **Separation of Concerns**: UI, ViewModel, Repository, DAO layers
2. **Reactive Programming**: Kotlin Flows throughout
3. **Type Safety**: Sealed interfaces for states
4. **Immutability**: Data classes, StateFlow
5. **Single Source of Truth**: Room database
6. **Efficient Rendering**: LazyVerticalGrid, key-based items
7. **Dependency Injection**: Hilt for loose coupling
8. **Error Handling**: Try-catch, error states
9. **Loading States**: User feedback during operations
10. **Material Design 3**: Modern UI guidelines

## 🚀 Performance Optimizations

- ✅ Flow-based database queries (no blocking)
- ✅ Coil image caching (memory + disk)
- ✅ Lazy grid rendering (visible items only)
- ✅ Key-based recomposition (minimal updates)
- ✅ Crossfade animations (smooth transitions)
- ✅ Background thread scanning (Dispatchers.IO)
- ✅ Adaptive grid columns (responsive layout)

---

**Ready for production! 🎉**

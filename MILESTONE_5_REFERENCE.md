# Milestone 5: Quick Reference Guide

## 🎯 What Changed

### Dependencies Added
```gradle
// Paging 3
implementation("androidx.paging:paging-runtime:3.2.1")
implementation("androidx.paging:paging-compose:3.2.1")
implementation("androidx.room:room-paging:2.6.1")

// Hilt Worker
implementation("androidx.hilt:hilt-work:1.1.0")
kapt("androidx.hilt:hilt-compiler:1.1.0")
```

## 🔑 Key Classes

### Paging
- `MediaDao.getPagedMedia()` - Returns PagingSource
- `MediaRepository.getPagedMedia()` - Returns Flow<PagingData>
- `GalleryViewModel.pagedMedia` - Exposes paging data to UI
- `PagedGalleryGrid` - Composable with LazyPagingItems

### Differential Sync
- `DifferentialSyncEngine` - Core sync logic
- `ScanMetadata` - Tracks last scan hash/timestamp
- `MediaSyncResult` - Contains new/deleted/updated items

### Background Sync
- `MediaSyncWorker` - WorkManager worker
- `MediaSyncScheduler` - Scheduling helper
- Runs every 3 hours when device idle

### Caching
- `ImageLoadingModule` - Coil configuration
- `ThumbnailLoader` - Optimized image requests
- 512MB disk + 25% memory cache

## 📊 Database Schema v4

```sql
CREATE TABLE media_items (
    id INTEGER PRIMARY KEY,
    uri TEXT NOT NULL,
    dateTaken INTEGER NOT NULL,
    bucket TEXT NOT NULL,
    size INTEGER NOT NULL,
    width INTEGER DEFAULT 0,
    height INTEGER DEFAULT 0,
    orientation INTEGER DEFAULT 0,
    mimeType TEXT DEFAULT 'image/jpeg',
    addedTimestamp INTEGER DEFAULT 0
);

CREATE INDEX idx_date_taken ON media_items(dateTaken);
CREATE INDEX idx_bucket ON media_items(bucket);
CREATE INDEX idx_bucket_date ON media_items(bucket, dateTaken);
CREATE INDEX idx_added_timestamp ON media_items(addedTimestamp);
```

## 🚀 Usage Examples

### Force Full Sync
```kotlin
viewModel.scanImages(forceFull = true)
```

### Schedule Immediate Background Sync
```kotlin
syncScheduler.scheduleImmediateSync()
```

### Load Full Image (not thumbnail)
```kotlin
ThumbnailLoader.createFullImageRequest(context, mediaId)
```

### Query Recently Added
```kotlin
mediaDao.getRecentlyAdded(limit = 50)
```

## 🐛 Debugging

### Check Paging Status
```kotlin
lazyPagingItems.loadState.refresh // Initial load
lazyPagingItems.loadState.append  // Loading more
```

### View Sync Logs
```bash
adb logcat | grep -E "DifferentialSync|MediaSyncWorker"
```

### Check WorkManager Status
```bash
adb shell dumpsys jobscheduler | grep gallery_app
```

### Inspect Database
```bash
adb shell
cd /data/data/com.gallery_app/databases
sqlite3 media_db
.schema media_items
```

## ⚡ Performance Tips

1. **Paging Config** - Adjust pageSize/prefetchDistance in MediaRepositoryImpl
2. **Cache Size** - Modify disk/memory cache in ImageLoadingModule
3. **Sync Frequency** - Change interval in MediaSyncScheduler
4. **Thumbnail Size** - Adjust targetSize in ThumbnailLoader

## 🔄 Migration Path

- v1 → v2: Adds scan_metadata table
- v2 → v3: Adds database indexes
- v3 → v4: Adds metadata columns (width, height, etc.)

All migrations are automatic. Fallback to destructive if migration fails.

## 📱 Testing Checklist

- [ ] Scroll through 1000+ images smoothly
- [ ] Add new image, refresh, verify quick sync
- [ ] Delete image, refresh, verify removal
- [ ] Leave app idle for 3+ hours, check background sync
- [ ] Clear app data, verify thumbnails cache properly
- [ ] Check memory usage stays under 250MB

## 🎨 UI Components

### Shimmer Loading
Animated placeholder shown while images load in paging grid.

### Paged Grid
Efficient LazyVerticalGrid that loads items on demand.

### Thumbnail Optimization
400x400 optimized images with proper cache keys.

---

**All systems operational ✅**

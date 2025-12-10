package com.gallery_app.data.sync

import android.content.Context
import android.util.Log
import com.gallery_app.data.GalleryImage
import com.gallery_app.data.MediaScanner
import com.gallery_app.data.db.MediaEntity
import com.gallery_app.data.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifferentialSyncEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanner: MediaScanner,
    private val mediaRepository: MediaRepository,
    private val scanMetadataDao: ScanMetadataDao
) {
    companion object {
        private const val TAG = "DifferentialSync"
    }

    /**
     * Performs differential sync:
     * 1. Gets current device media
     * 2. Compares with DB
     * 3. Returns only differences
     */
    suspend fun performDifferentialSync(): MediaSyncResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting differential sync...")
        
        // Get current device media
        val currentMedia = scanner.loadImages()
        val currentMediaMap = currentMedia.associateBy { it.id }
        
        // Get existing DB media (as a list, convert to map)
        val existingEntities = mutableListOf<MediaEntity>()
        mediaRepository.getAllMedia().collect { list ->
            existingEntities.clear()
            existingEntities.addAll(list)
        }
        val existingMap = existingEntities.associateBy { it.id }
        
        // Find new items (in current but not in DB)
        val newItems = currentMedia.filter { it.id !in existingMap }
        
        // Find deleted items (in DB but not in current)
        val deletedIds = existingMap.keys.filter { it !in currentMediaMap }
        
        // Find updated items (same ID but different size or timestamp)
        val updatedItems = currentMedia.filter { current ->
            existingMap[current.id]?.let { existing ->
                existing.size != current.size || existing.dateTaken != current.dateTaken
            } ?: false
        }
        
        val unchanged = currentMedia.size - newItems.size - updatedItems.size
        
        Log.d(TAG, "Sync complete: ${newItems.size} new, ${deletedIds.size} deleted, ${updatedItems.size} updated, $unchanged unchanged")
        
        // Save scan metadata
        val timestamp = System.currentTimeMillis()
        val hash = computeHash(currentMedia.map { it.id.toString() })
        scanMetadataDao.saveMetadata(ScanMetadata(1, timestamp, hash))
        
        MediaSyncResult(
            newItems = newItems,
            deletedIds = deletedIds.toList(),
            updatedItems = updatedItems,
            unchanged = unchanged
        )
    }

    /**
     * Apply sync result to database
     */
    suspend fun applySyncResult(result: MediaSyncResult) = withContext(Dispatchers.IO) {
        // Delete removed items
        if (result.deletedIds.isNotEmpty()) {
            mediaRepository.deleteByIds(result.deletedIds)
            Log.d(TAG, "Deleted ${result.deletedIds.size} items")
        }
        
        // Insert new items
        if (result.newItems.isNotEmpty()) {
            val newEntities = result.newItems.map { image ->
                MediaEntity(
                    id = image.id,
                    uri = image.uri,
                    dateTaken = image.dateTaken,
                    bucket = image.bucketName,
                    size = image.size
                )
            }
            mediaRepository.insertAll(newEntities)
            Log.d(TAG, "Inserted ${newEntities.size} new items")
        }
        
        // Update changed items (replace)
        if (result.updatedItems.isNotEmpty()) {
            val updatedEntities = result.updatedItems.map { image ->
                MediaEntity(
                    id = image.id,
                    uri = image.uri,
                    dateTaken = image.dateTaken,
                    bucket = image.bucketName,
                    size = image.size
                )
            }
            mediaRepository.insertAll(updatedEntities) // REPLACE strategy
            Log.d(TAG, "Updated ${updatedEntities.size} items")
        }
        
        Log.d(TAG, "Applied sync result to database")
    }

    private fun computeHash(ids: List<String>): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = ids.sorted().joinToString(",")
        val hashBytes = digest.digest(combined.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Check if a full sync is needed
     */
    suspend fun shouldPerformFullSync(): Boolean {
        val metadata = scanMetadataDao.getMetadata()
        return metadata == null || 
               (System.currentTimeMillis() - metadata.lastScanTimestamp) > 7 * 24 * 60 * 60 * 1000L // 7 days
    }
}

package com.gallery_app.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gallery_app.data.sync.DifferentialSyncEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MediaSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncEngine: DifferentialSyncEngine
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "MediaSyncWorker"
        const val WORK_NAME = "media_background_sync"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting background media sync...")
            
            // Perform differential sync
            val result = syncEngine.performDifferentialSync()
            syncEngine.applySyncResult(result)
            
            Log.d(TAG, "Background sync complete: ${result.newItems.size} new, ${result.deletedIds.size} deleted, ${result.updatedItems.size} updated")
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Background sync failed", e)
            Result.retry()
        }
    }
}

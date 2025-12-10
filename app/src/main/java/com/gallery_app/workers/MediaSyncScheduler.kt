package com.gallery_app.workers

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule periodic background sync
     * Runs every 3 hours with constraints
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // No network needed for local scan
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true) // Only when device is idle
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<MediaSyncWorker>(
            3, TimeUnit.HOURS, // Repeat every 3 hours
            30, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(MediaSyncWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MediaSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if already scheduled
            syncRequest
        )
    }

    /**
     * Schedule one-time immediate sync
     */
    fun scheduleImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<MediaSyncWorker>()
            .setConstraints(constraints)
            .addTag(MediaSyncWorker.TAG)
            .build()

        workManager.enqueue(syncRequest)
    }

    /**
     * Cancel all scheduled syncs
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(MediaSyncWorker.WORK_NAME)
    }

    /**
     * Get sync status
     */
    fun getSyncStatus() = workManager.getWorkInfosForUniqueWorkLiveData(MediaSyncWorker.WORK_NAME)
}

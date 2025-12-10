package com.gallery_app.workers

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.gallery_app.data.sync.DifferentialSyncEngine
import javax.inject.Inject

class HiltWorkerFactory @Inject constructor(
    private val syncEngine: DifferentialSyncEngine
) : WorkerFactory() {
    
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            MediaSyncWorker::class.java.name -> {
                MediaSyncWorker(appContext, workerParameters, syncEngine)
            }
            else -> null
        }
    }
}

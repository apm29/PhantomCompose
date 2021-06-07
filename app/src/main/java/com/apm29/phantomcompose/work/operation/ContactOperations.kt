package com.apm29.phantomcompose.work.operation

import android.content.Context
import androidx.work.*
import com.apm29.phantomcompose.work.Constants
import com.apm29.phantomcompose.work.worker.DataSynchronizeWorker
import java.time.Duration
import java.util.*

class ContactOperations(
    lastSyncTime: Long,
) {
    private val coreData = workDataOf(Constants.KEY_LAST_SYNC_TIME to lastSyncTime)

    private val periodicWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<DataSynchronizeWorker>(
            repeatInterval = Duration.ofSeconds(50),
            flexTimeInterval = Duration.ofSeconds(30)
        ).setInputData(coreData)
            .setInitialDelay(Duration.ofSeconds(0))
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10))//每次重试间隔N*20sec
            .build()


    val uniqueWorkName = "sync"

    val id
        get() = periodicWorkRequest.id

    fun startSync(context: Context): Operation {
        return WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest,
        )
    }
}
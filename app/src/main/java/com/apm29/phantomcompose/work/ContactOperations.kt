package com.apm29.phantomcompose.work

import android.content.Context
import androidx.work.*
import java.time.Duration

class ContactOperations(
    lastSyncTime: Long
) {
    private val coreData = workDataOf(Constants.KEY_LAST_SYNC_TIME to lastSyncTime)

    private val periodicWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<DataSynchronizeWorker>(
            repeatInterval = Duration.ofSeconds(120),
            flexTimeInterval = Duration.ofSeconds(40)
        ).setInputData(coreData)
            .setInitialDelay(Duration.ofSeconds(30))
            .setConstraints(
                Constraints.Builder()
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(20))//每次重试间隔N*20sec
            .build()


    fun startSync(context: Context): Operation {
        return WorkManager.getInstance(context).enqueue(
            periodicWorkRequest
        )
    }
}
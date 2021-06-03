package com.apm29.phantomcompose.work

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apm29.phantomcompose.api.TestApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataSynchronizeWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParameters: WorkerParameters,
    private val testApi: TestApi
) : CoroutineWorker(context, workParameters) {

    init {
        Log.e(TAG, "INIT")
    }

    override suspend fun doWork(): Result {
        Log.e(TAG, "START")
        val res = testApi.test()
        toastAndLog(res.toString())
        Log.e(TAG, "FINISH")
        return Result.success()
    }

    private fun toastAndLog(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message)
    }

    companion object {
        private const val TAG = "DataSynchronizeTask"
    }
}
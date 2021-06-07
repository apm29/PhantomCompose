package com.apm29.phantomcompose.work.worker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.apm29.phantomcompose.api.TestApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class DataSynchronizeWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParameters: WorkerParameters,
    private val testApi: TestApi
) : CoroutineWorker(context, workParameters) {

    init {
        Log.e(TAG, "INIT")
    }

    private val handler = Handler(Looper.getMainLooper())

    override suspend fun doWork(): Result {
        Log.e(TAG, "START")
        val res = testApi.test()
        (1..30000).forEach {
            setProgress(workDataOf("progress" to it))
            delay(10)
        }
        toastAndLog(res.toString())
        Log.e(TAG, "FINISH")
        return Result.success()
    }

    private fun toastAndLog(message: String) {
        handler.post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
        Log.e(TAG, message)
    }

    companion object {
        private const val TAG = "DataSynchronizeTask"
    }
}
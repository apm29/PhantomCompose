package com.apm29.phantomcompose.work

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.apm29.phantomcompose.api.TestApi
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DataSynchronizeWorker(context: Context, workParameters: WorkerParameters) :
    Worker(context, workParameters) {

    @Inject
    lateinit var testApi: TestApi

    init {
        Log.e(TAG,"INIT")
    }

    override fun doWork(): Result {
        Log.e(TAG,"START")
        runBlocking {
            val res = testApi.test()
            toastAndLog(res.toString())
        }
        Log.e(TAG,"FINISH")
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
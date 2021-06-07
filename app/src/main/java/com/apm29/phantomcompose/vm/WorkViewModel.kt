package com.apm29.phantomcompose.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.apm29.phantomcompose.api.TestApi
import com.apm29.phantomcompose.work.operation.ContactOperations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    val testApi: TestApi, @ApplicationContext context: Context
) : ViewModel() {
    private val contactOperations = ContactOperations(0L)
    private val syncOperation: Operation = contactOperations.startSync(context)

    private val workManager: WorkManager = WorkManager.getInstance(context)

    val syncWorkerState: LiveData<MutableList<WorkInfo>>
        get() = workManager.getWorkInfosForUniqueWorkLiveData(contactOperations.uniqueWorkName)
}
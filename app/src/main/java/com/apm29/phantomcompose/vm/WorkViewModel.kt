package com.apm29.phantomcompose.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Operation
import com.apm29.phantomcompose.api.TestApi
import com.apm29.phantomcompose.work.operation.ContactOperations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    val testApi: TestApi, @ApplicationContext context: Context
) : ViewModel() {
    private val syncOperation: Operation = ContactOperations(0L).startSync(context)

    val syncWorkerState
        get() = syncOperation.state
}
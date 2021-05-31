package com.apm29.phantomcompose.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apm29.phantomcompose.model.IntervieweeDetail
import com.apm29.phantomcompose.model.VisitRecord
import com.apm29.phantomcompose.model.VisitorDetail
import com.apm29.phantomcompose.repo.VisitorRepository

class EnterRegisterViewModel(private val visitorRepository: VisitorRepository) : ViewModel() {

    //提交访客、受访人信息
    fun onSubmitRegister(visitorDetail: VisitorDetail, intervieweeDetail: IntervieweeDetail) {

    }

    fun onReject() {

    }

    //访客记录部分
    private val _visitRecords = mutableStateListOf<VisitRecord>()
    val visitorRecords: List<VisitRecord> = _visitRecords
    var visitRecordsPage: Int = 1
        private set
    var visitRecordsRows: Int = 20
        private set

    var hasMoreVisitRecords: Boolean by mutableStateOf(true)
        private set
    var loadingVisitRecords: Boolean by mutableStateOf(false)
        private set

    suspend fun getVisitRecords() {
        try {
            loadingVisitRecords = true
            _visitRecords.addAll(
                visitorRepository.getVisitorRecords(
                    visitRecordsPage,
                    visitRecordsRows
                )
            )
            visitRecordsPage += 1
            hasMoreVisitRecords = visitRecordsPage <= 5
        } finally {
            loadingVisitRecords = false
        }
    }

    @Suppress("UNCHECKED_CAST")
    class EnterRegisterViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EnterRegisterViewModel::class.java)) {
                return EnterRegisterViewModel(VisitorRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}
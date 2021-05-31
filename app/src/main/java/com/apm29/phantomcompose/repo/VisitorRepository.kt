package com.apm29.phantomcompose.repo

import com.apm29.phantomcompose.model.VisitRecord
import kotlinx.coroutines.delay

object VisitorRepository {

    suspend fun getVisitorRecords(page: Int, rows: Int): List<VisitRecord> {
        delay(1000)
        return ((page - 1) * rows until page * rows).map {
            VisitRecord(
                "Test-$it",
                "XX老师",
                "18:09",
                "19:09",
                "01:00",
            )
        }
    }
}
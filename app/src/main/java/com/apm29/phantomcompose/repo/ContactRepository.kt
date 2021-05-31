package com.apm29.phantomcompose.repo

import com.apm29.phantomcompose.model.Contacts
import com.apm29.phantomcompose.model.VisitRecord
import kotlinx.coroutines.delay

object ContactRepository {

    suspend fun getContactRecords(page: Int, rows: Int): List<Contacts> {
        delay(1000)
        return ((page - 1) * rows until page * rows).map {
            Contacts(
                "XX老师-$it",
                "1878-5731-$it",
                "XXXXX 4-2-302"
            )
        }
    }
}
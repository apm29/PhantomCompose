package com.apm29.phantomcompose.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apm29.phantomcompose.model.Contacts
import com.apm29.phantomcompose.repo.ContactRepository

class ContactViewModel(private val contactRepository: ContactRepository) : ViewModel() {


    //访客记录部分
    private val _visitRecords = mutableStateListOf<Contacts>()
    val contactRecords: List<Contacts> = _visitRecords
    var visitRecordsPage: Int = 1
        private set
    var visitRecordsRows: Int = 20
        private set

    var hasMoreContacts: Boolean by mutableStateOf(true)
        private set
    var loadingContacts: Boolean by mutableStateOf(false)
        private set

    suspend fun getContacts() {
        try {
            loadingContacts = true
            _visitRecords.addAll(
                contactRepository.getContactRecords(
                    visitRecordsPage,
                    visitRecordsRows
                )
            )
            visitRecordsPage += 1
            hasMoreContacts = visitRecordsPage <= 5
        } finally {
            loadingContacts = false
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ContactsViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
                return ContactViewModel(ContactRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}
package com.apm29.phantomcompose

import com.apm29.phantomcompose.repo.ContactRepository
import com.apm29.phantomcompose.vm.ContactViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @InternalCoroutinesApi
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val contactViewModel = ContactViewModel(ContactRepository)
        runBlocking {
            contactViewModel.getContacts()
            contactViewModel.getContacts()
        }
        assertEquals(40,contactViewModel.contactRecords.size)
    }
}
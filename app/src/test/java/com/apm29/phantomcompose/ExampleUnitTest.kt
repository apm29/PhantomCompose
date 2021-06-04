package com.apm29.phantomcompose

import com.apm29.phantomcompose.api.Constants
import com.apm29.phantomcompose.api.TestApi
import com.apm29.phantomcompose.repo.ContactRepository
import com.apm29.phantomcompose.vm.ContactViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        assertEquals(40, contactViewModel.contactRecords.size)


        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(
                        HttpLoggingInterceptor()
                            .apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                    )
                    .build()
            )
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()

        val test = retrofit.create(TestApi::class.java)

        runBlocking {
            val resp = test.test()
            println(resp)
        }
    }


}
package com.apm29.phantomcompose.di

import com.apm29.phantomcompose.api.Constants
import com.apm29.phantomcompose.api.TestApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DependencyInject {
    @Provides
    @Singleton
    fun provideOkhttp():OkHttpClient{
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideTestApi(retrofit: Retrofit):TestApi{
        return  retrofit.create(TestApi::class.java)
    }
}
package com.apm29.phantomcompose.api

import com.apm29.phantomcompose.model.BaseResp
import retrofit2.http.POST

interface TestApi {

    @POST("/java/auth/getAllSchool")
    suspend fun test(): BaseResp<Any>

}
package com.apm29.phantomcompose.model

/**
 *  author : apm29[ciih]
 *  date : 2020/10/9 9:52 AM
 *  description :
 */
data class BaseResp<T>(
    val code:Int = 1,
    val msg
    :String = "success",
    val data:T? = null
){
    val success:Boolean
        get() = code == 1

    val hasData:Boolean
        get() = data != null
}
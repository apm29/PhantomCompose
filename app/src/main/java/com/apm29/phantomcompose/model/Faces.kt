package com.apm29.phantomcompose.model

data class FaceCommonInfo(
    val id:Int,
    val gender:String,
    val age:Int,
    val yaw:Float,
    val roll:Float,
    val pitch:Float,
    val liveness:String
)
package com.apm29.phantomcompose.model

data class FaceCommonInfo(
    val id:Int,
    val gender:String,
    val age:Int,
    val yaw:Float,
    val roll:Float,
    val pitch:Float,
    val liveness:String
){
    override fun toString(): String {
        return "id：$id, 性别：$gender, 年龄：$age,\n 偏航角：$yaw,\n 滚动角：$roll,\n 俯仰角：$pitch,\n 活体：$liveness"
    }
}
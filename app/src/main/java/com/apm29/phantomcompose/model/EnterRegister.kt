package com.apm29.phantomcompose.model

data class VisitorDetail(
    val name:String,
    val gender:Int,
    val idCard:String,
    val phone:String,
    val address:String,
    val company:String,
    val guestId:String,
    val passId:String,
    val count:Int,
    val reason:String,
    val vehicleLicense:String
)

data class IntervieweeDetail(
    val name:String
)

sealed class Gender(
    val value:Int,
    val text:String
){
    object Male :Gender(0,"男")
    object Female :Gender(1,"女")
}
package com.apm29.telpo.utils

import com.telpo.servicelibrary.IdcardMsg

fun IdcardMsg.asText():String{
    return "姓名：${this.name} \r\n身份证号：${this.no}\r\n性别：${this.sex}"
}
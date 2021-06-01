package com.apm29.telpo

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.telpo.servicelibrary.Idcard2And1Util
import com.telpo.servicelibrary.IdcardMsg

class IdCardFragment: Fragment() {


    lateinit var idCardUtils:Idcard2And1Util

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(object:LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate(){
                idCardUtils = Idcard2And1Util.getInstance(requireContext(),"")
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy(){
                idCardUtils.release()
            }
        })
    }

    fun checkIdCard():IdcardMsg?{
        return idCardUtils.checkIDCard()
    }

    fun decodeIdImage(idCardMsg: IdcardMsg):Bitmap{
        return idCardUtils.decodeIDImage(idCardMsg)
    }

    //身份证的操作流程有3步，寻卡---选卡---读卡，此流程会返回身份证的字节内容
    //寻找身份证
     fun findIDCard():Boolean{
         return idCardUtils.findIDCard()
     }

    //选择身份证
    fun selectIDCard():Boolean{
        return idCardUtils.selectIDCard()
    }

    //读身份证
    fun readIDCard():ByteArray{
        return idCardUtils.readIDCard()
    }

}
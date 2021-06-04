package com.apm29.phantomcompose.vm

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class QRCodeViewModel @Inject constructor(
   @ApplicationContext context: Context
) :ViewModel(){

    private val toast:Toast = Toast.makeText(context,"",Toast.LENGTH_LONG)

    private val _code:MutableLiveData<String> = MutableLiveData()

    val code:LiveData<String>
        get() = _code

    fun setCode(code:String?){
        _code.value = code
        toast.setText(code)
        toast.show()
    }

}
package com.zaz.support.utils

import android.os.Handler
import android.os.Looper

object ThreadPool {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun main(action:()->Unit){
        if(Looper.myLooper() == Looper.getMainLooper()){
            action()
        }else{
            mainHandler.post(action)
        }
    }
}
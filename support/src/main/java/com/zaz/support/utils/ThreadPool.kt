package com.zaz.support.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor

object ThreadPool {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun main(action:()->Unit){
        if(Looper.myLooper() == Looper.getMainLooper()){
            action()
        }else{
            mainHandler.post(action)
        }
    }

    fun mainDelay(delay:Long,action:()->Unit){
        mainHandler.postDelayed(action,delay)
    }

}
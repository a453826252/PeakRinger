package com.zaz.support.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

object PRToast {
    private const val TAG = "PRToast"
    fun show(context: Context,msg:String){
        if(TextUtils.isEmpty(msg)){
            Log.e(TAG, "show: msg is empty")
            return
        }
        ThreadPool.main {
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
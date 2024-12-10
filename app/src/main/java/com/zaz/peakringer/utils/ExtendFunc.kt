package com.zaz.peakringer.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.peakringer.manager.ListenContactsSwitchStateManager
import com.zaz.support.utils.SpUtils

fun Fragment.startFragment(type: Int,data:Bundle?=null) {
    startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
        putExtra(CommonActivity.FRAGMENT_TYPE_KEY, type)
        data?.let {
            putExtra("extra",it)
        }
    })
}

fun Context.isFeatureOpen():Boolean{
    with(SpUtils.getPrConfigInstance(this)){
        val open = getBoolean(SpUtils.FEATURE_OPEN,true)
        val openActual = if(open){
            true
        }else{
            val autoOpenTime = autoOpenTime()
            val currentTime = System.currentTimeMillis() / 1000
            Log.d("isFeatureOpen", "isFeatureOpen: currentTime=$currentTime,autoOpenTime=$autoOpenTime")
            if(autoOpenTime in 1 .. currentTime){
                enableFeature()
                true
            }else{
                false
            }
        }
        Log.d("isFeatureOpen", "isFeatureOpen:$openActual")
        return openActual
    }

}
fun Context.enableFeature(){
    changeOpen(this,true)
}
fun Context.disableFeature(){
    changeOpen(this,false)
}

fun Context.disableFeatureTemporary(temporaryTime: Int){
    Log.d("disableFeatureTemporary", "disableFeatureTemporary:temporaryTime=$temporaryTime")
    disableFeatureBefore(System.currentTimeMillis() / 1000 + temporaryTime)
}

fun Context.disableFeatureBefore(time: Long){
    val currentTime = System.currentTimeMillis() / 1000
    if(time < currentTime){
        Log.e("disableFeatureBefore", "error when set auto open time,time($time) < currentTime($time)")
        return
    }
    Log.d("disableFeatureBefore", "disableFeatureBefore:time=$time")
    SpUtils.getPrConfigInstance(this).putBoolean(SpUtils.FEATURE_OPEN,false).putLong(SpUtils.AUTO_OPEN_AT,time)
    ListenContactsSwitchStateManager.onStateChanged(false,time)
}
private fun changeOpen(context: Context,open:Boolean){
    Log.d("changeOpen", "open=$open")
    SpUtils.getPrConfigInstance(context)
        .putBoolean(SpUtils.FEATURE_OPEN,open)
        .remove(SpUtils.AUTO_OPEN_AT)
    ListenContactsSwitchStateManager.onStateChanged(open)
}

fun Context.autoOpenTime() = SpUtils.getPrConfigInstance(this).getLong(SpUtils.AUTO_OPEN_AT,-1)

val String.formatToPhoneNumber
    get() = PRPhoneNumberUtil.format(this)
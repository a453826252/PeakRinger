package com.zaz.support.utils

import android.content.Context
import com.zaz.support.R
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

object TimeUtils {
    /**
     * 给定时间距现在的时间，距离在1天内精确到分钟，1天以上精确到天
     * 例如：3天5小时23分钟(前/后)
     * 超过7天使用绝对时间，yyyy-MM-dd
     */
    fun getFormatTimeFromNow(context: Context,time: Long):String{
        val currentTime = System.currentTimeMillis() / 1000
        val timeDistance = abs(currentTime - time)
        if(timeDistance > 7 * 86400){
            //超过7天
            return SimpleDateFormat("yyyy-MM-dd").format(time * 1000)
        }else if(timeDistance > 86400){
            return (timeDistance / 86400).toString() + context.getString(R.string.time_day)
        }else{
            val hour = timeDistance / 3600
            val min  = timeDistance % 3600 / 60
            val sec = timeDistance % 60
            val stringBuilder = StringBuilder()
            if(hour > 0){
                stringBuilder.append(hour.toString() + " "+ context.getString(R.string.time_hour) + " ")
            }
            if(min > 0){
                stringBuilder.append(min.toString() + " " +context.getString(R.string.time_min) + " ")
            }
            if(sec > 0 && stringBuilder.isEmpty()){
                stringBuilder.append(sec.toString() + " " + context.getString(R.string.time_sec))
            }
            return  stringBuilder.toString()
        }
    }
}
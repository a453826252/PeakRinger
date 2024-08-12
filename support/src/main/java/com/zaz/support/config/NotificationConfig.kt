package com.zaz.support.config

import android.app.PendingIntent
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

data class NotificationConfig(
    @DrawableRes
    val smallIcon:Int,
    val title:String,
    val content:String,
    val notificationId:Int
){
    var btnAction:NotificationCompat.Action?=null  //在通知栏展示一个安妮
    var clickAction:PendingIntent?=null
    var onGoing = false
    var currentProgress:Int?=null
}

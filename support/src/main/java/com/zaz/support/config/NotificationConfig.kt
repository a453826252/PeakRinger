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
    var btnYesAction:NotificationCompat.Action?=null  //在通知栏展示一个按钮
    var btnNoAction:NotificationCompat.Action?=null  //在通知栏展示一个按钮
    var clickAction:PendingIntent?=null
    var onGoing = false
    var currentProgress:Int?=null
}

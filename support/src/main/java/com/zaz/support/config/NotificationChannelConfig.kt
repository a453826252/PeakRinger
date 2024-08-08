package com.zaz.support.config

data class NotificationChannelConfig(
    val channelId:String,
    val channelName:String,
    val channelImportance:Int,
    val channelDesc:String = ""
)

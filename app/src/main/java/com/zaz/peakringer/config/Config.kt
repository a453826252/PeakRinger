package com.zaz.peakringer.config

import android.content.Context

object Config {
    fun getAvatarDefaultPath(context: Context,name:String,phoneNum:String,mimeType:String) = "${context.filesDir}/avatar/_${name}_${phoneNum}_${System.currentTimeMillis()}.$mimeType}"
    fun getAvatarCachePath(context: Context,avatarName:String) = "${context.cacheDir}/avatar/$avatarName"
    fun getAvatarWaitCropPath(context: Context,avatarName:String) = "${context.cacheDir}/avatar_wait_crop/$avatarName"
}
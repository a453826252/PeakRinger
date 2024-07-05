package com.zaz.peakringer.config

import android.content.Context

object Config {
    fun getAvatarDefaultPath(context: Context,avatarName:String) = "${context.filesDir}/avatar/$avatarName"
}
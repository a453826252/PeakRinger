package com.zaz.update

import android.content.Context

interface IUpdate {
    fun checkUpdate(context: Context,resultCallback:(UpdateCheckResult)->Unit)

    fun startUpdate(type:UpdateCheckResult, startUpdateConfig: StartUpdateConfig)

    fun install(context: Context):Boolean
}
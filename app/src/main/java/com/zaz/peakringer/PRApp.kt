package com.zaz.peakringer

import android.app.Application
import com.zaz.support.AppGlobal

class PRApp: Application() {
    companion object{
        @JvmStatic
        lateinit var application: Application
    }
    override fun onCreate() {
        super.onCreate()
        application = this
        AppGlobal.application = application
    }
}
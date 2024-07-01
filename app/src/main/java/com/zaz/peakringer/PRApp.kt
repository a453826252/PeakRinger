package com.zaz.peakringer

import android.app.Application

class PRApp: Application() {
    companion object{
        lateinit var application: Application
    }
    override fun onCreate() {
        super.onCreate()
        application = this
    }
}
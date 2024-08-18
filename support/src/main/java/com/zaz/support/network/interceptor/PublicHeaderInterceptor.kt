package com.zaz.support.network.interceptor

import android.content.Context
import android.os.Build
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class PublicHeaderInterceptor(val context: Context):Interceptor {
    companion object{
        private const val TAG = "PublicHeaderInterceptor"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val headers = JSONObject()
       val language =  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            context.resources.configuration.locales[0].language
        }else{
           context.resources.configuration.locale.language
        }
        headers.put("language",language)
        requestBuilder.addHeader("publicHeader",headers.toString())
        Log.d(TAG, "PublicHeaderInterceptor, headers=$headers")
        return chain.proceed(requestBuilder.build())
    }
}
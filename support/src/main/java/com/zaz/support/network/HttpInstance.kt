package com.zaz.support.network

import android.util.Log
import com.zaz.support.network.calladapter.FlowCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpInstance {
    private const val TAG = "HttpInstance"
    fun <T> getService(clazz:Class<T>):T = instance.create(clazz)

    private const val BASE_URL = "https://www.peakringer.com"
    private val instance = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor{
                    Log.d(TAG, it)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
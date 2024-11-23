package com.zaz.support.network.interceptor

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.zaz.support.utils.SpUtils
import com.zaz.support.utils.myVerCode
import com.zaz.support.utils.myVerName
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.util.Locale

class PublicHeaderInterceptor(val context: Context):Interceptor {
    companion object{
        private const val TAG = "PublicHeaderInterceptor"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val headers = JSONObject()
       val locale =  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            context.resources.configuration.locales[0]
        }else{
           context.resources.configuration.locale
        }
        val country = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso.uppercase(Locale.ROOT)
        headers.put("language",locale.language)
        headers.put("country",country)
        headers.put("initVerCode",SpUtils.getPrConfigInstance(context).getInt(SpUtils.INIT_VERSION,0))
        headers.put("currentVerCode",context.myVerCode)
        headers.put("currentVerName",context.myVerName)
        headers.put("brand",Build.BRAND)
        headers.put("api",Build.VERSION.SDK_INT)
        headers.put("manufacturer",Build.MANUFACTURER)
        headers.put("model",Build.MODEL)
        requestBuilder.addHeader("publicHeader",headers.toString())
        Log.d(TAG, "PublicHeaderInterceptor, headers=$headers")
        return chain.proceed(requestBuilder.build())
    }
}
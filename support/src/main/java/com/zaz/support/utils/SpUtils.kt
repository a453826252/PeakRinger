package com.zaz.support.utils

import android.content.Context
import android.content.SharedPreferences

object SpUtils {
    private val spCache:MutableMap<String,Sp> = mutableMapOf()
    fun getInstance(context: Context,spName: String):Sp{
        if(!spCache.containsKey(spName)){
            val sp = Sp(context, spName)
            spCache[spName] = sp
        }
        return spCache[spName]!!
    }
    class Sp(context: Context,spName:String){
        private val spInstance:SharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE)
        fun put(key:String, value:Int):Sp{
            spInstance.edit().putInt(key,value).apply()
            return this
        }
        fun get(key:String,default:Int):Int{
            return spInstance.getInt(key,default)
        }
        fun put(key:String,value:Boolean):Sp{
            spInstance.edit().putBoolean(key,value).apply()
            return this
        }
        fun get(key:String,default:Boolean):Boolean{
            return spInstance.getBoolean(key,default)
        }
    }
}
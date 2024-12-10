package com.zaz.support.utils

import android.content.Context
import android.content.SharedPreferences

object SpUtils {
    const val SP_NAME_PR_CONFIG = "pr_config"
    const val PRIVACY_AGREED = "privacy_agreed"
    const val INIT_VERSION = "init_version"
    const val FEATURE_OPEN = "feature_open"
    const val AUTO_OPEN_AT = "auto_open_at"
    private val spCache:MutableMap<String,Sp> = mutableMapOf()
    fun getInstance(context: Context,spName: String):Sp{
        if(!spCache.containsKey(spName)){
            val sp = Sp(context, spName)
            spCache[spName] = sp
        }
        return spCache[spName]!!
    }

    fun getPrConfigInstance(context: Context):Sp{
        return getInstance(context, SP_NAME_PR_CONFIG)
    }
    class Sp(context: Context,val spName:String){
        private val spInstance:SharedPreferences = context.getSharedPreferences(spName,Context.MODE_PRIVATE)
        fun putInt(key:String, value:Int):Sp{
            spInstance.edit().putInt("${spName}_$key",value).apply()
            return this
        }
        fun getInt(key:String, default:Int):Int{
            return spInstance.getInt("${spName}_$key",default)
        }
        fun putBoolean(key:String, value:Boolean):Sp{
            spInstance.edit().putBoolean("${spName}_$key",value).apply()
            return this
        }
        fun getBoolean(key:String, default:Boolean):Boolean{
            return spInstance.getBoolean("${spName}_$key",default)
        }

        fun putLong(key:String, value:Long):Sp{
            spInstance.edit().putLong("${spName}_$key",value).apply()
            return this
        }
        fun getLong(key:String,default:Long):Long{
            return spInstance.getLong("${spName}_$key",default)
        }

        fun remove(key: String){
            spInstance.edit().remove("${spName}_$key").apply()
        }
    }
}
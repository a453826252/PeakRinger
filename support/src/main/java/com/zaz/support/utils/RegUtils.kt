package com.zaz.support.utils

object RegUtils {
    fun isEmail(address:String):Boolean{
        if(address.isBlank()){
            return false
        }
        val regex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
        return  regex.matches(address)
    }
}
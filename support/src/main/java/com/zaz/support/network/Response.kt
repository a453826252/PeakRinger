package com.zaz.support.network

import com.google.gson.Gson

class Response<T> {
    var code = 0
    var msg:String?=""
    var data:T?=null
    var ex:Throwable?=null
    @Transient
    private val SUCCESS_CODE = 1
    fun isSuccessful() = code == SUCCESS_CODE

    override fun toString(): String {
       return Gson().toJson(this)
    }
}
package com.zaz.support.network.calladapter

import com.zaz.support.network.Response
import com.zaz.support.network.ResponseFlow
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }
        check(returnType is ParameterizedType) { "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>" }
        val responseType = getParameterUpperBound(0, returnType)
        check(getRawType(responseType) == Response::class.java && responseType is ParameterizedType) { "Response must be com.zaz.support.network.Response and must be parameterized as Response<Foo> or Response<out Foo>" }
       return ResponseCallAdapter<Any>(
           responseType
        )
    }

    companion object {
        @JvmStatic
        fun create() = FlowCallAdapterFactory()
    }
}
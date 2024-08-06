package com.zaz.support.network.calladapter

import com.zaz.support.network.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<Response<T>, Flow<Response<T>>> {

    override fun responseType() = responseType
    override fun adapt(call: Call<Response<T>>): Flow<Response<T>> {
        return flow {
            suspendCancellableCoroutine { continuation ->
                //协程取消时，调用call.cancel()取消call
                continuation.invokeOnCancellation {
                    call.cancel()
                }
                try {
                    //执行call.execute()
                    val response = call.execute()
                    //恢复执行，并返回Response
                    continuation.resume(response)
                } catch (e: Exception) {
                    //捕获异常，恢复执行，并返回异常
                    continuation.resumeWithException(e)
                }
            }.let { response ->
                //通过flow发射Response
                emit(response.body()!!)
            }
        }
            .catch {
                val response = Response<T>().apply {
                    code = -2
                    msg = it.message
                    ex = it
                }
                emit(response)
            }
            .flowOn(Dispatchers.IO)
    }
}
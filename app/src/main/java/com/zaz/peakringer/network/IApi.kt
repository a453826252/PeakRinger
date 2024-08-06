package com.zaz.peakringer.network

import com.zaz.peakringer.bean.FeedbackTypeBean
import com.zaz.support.network.Response
import com.zaz.support.network.ResponseFlow
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IApi {
    @GET("/feedback/getFeedbackType")
    fun getFeedbackType(): Flow<Response<List<FeedbackTypeBean>>>


    @POST("/feedback/submit")
    @FormUrlEncoded
    fun postFeedback(@Field("type") type:Int,@Field("content") content:String,@Field("contactInfo") contactInfo:String?):Flow<Response<Any?>>
}
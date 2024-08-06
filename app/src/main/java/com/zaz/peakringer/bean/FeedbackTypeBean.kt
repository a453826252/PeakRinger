package com.zaz.peakringer.bean

import com.google.gson.annotations.SerializedName
import com.zaz.support.dialog.bottom.IBottomItemBean

data class FeedbackTypeBean(
    val id:Int,
    @SerializedName("txt")
    val showTxt:String
): IBottomItemBean<FeedbackTypeBean> {
    override fun getContent(): String  = showTxt
}

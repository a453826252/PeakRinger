package com.zaz.peakringer.bean

import com.google.gson.annotations.SerializedName
import com.zaz.support.dialog.bottom.IBottomItemBean

data class StringItemBean(
    val id:Int,
    @SerializedName("txt")
    val showTxt:String
): IBottomItemBean<StringItemBean> {
    override fun getContent(): String  = showTxt

    object FeedBackType{
        const val TYPE_SUGGESTION = 1
        const val TYPE_ISSUE = 2
    }
    object PowerCloseType{
        const val TYPE_CLOSE_NOW = 1
        const val TYPE_CLOSE_MIN_30 = 2
        const val TYPE_CLOSE_MIN_60 = 3
        const val TYPE_CLOSE_HOUR_3 = 4
        const val TYPE_CLOSE_CUSTOM = 5
        const val TYPE_OPEN = 6
    }
}

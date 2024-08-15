package com.zaz.peakringer.fragment.setting

import android.content.Context
import com.zaz.peakringer.R
import com.zaz.support.base.BaseViewModel

class SettingItemVM: BaseViewModel() {
    fun getSettingItems(context: Context):MutableList<SettingItemBean>{
        val result = mutableListOf<SettingItemBean>()
        // 开关
        val toggle = SettingItemBean(SettingItemBean.ID_TOGGLE, R.mipmap.ic_power,context.getString(R.string.close))
        result.add(toggle)

        //反馈
        val feedback = SettingItemBean(SettingItemBean.ID_FEEDBACK,R.mipmap.ic_feedback,context.getString(R.string.feedback))
        result.add(feedback)
        return result
    }
}
package com.zaz.peakringer.fragment.setting

import android.content.Context
import com.zaz.peakringer.R
import com.zaz.peakringer.utils.isFeatureOpen
import com.zaz.support.base.BaseViewModel
import com.zaz.support.utils.SpUtils

class SettingItemVM: BaseViewModel() {
    fun getSettingItems(context: Context):MutableList<SettingItemBean>{
        val result = mutableListOf<SettingItemBean>()
        // 开关
        val toggle = SettingItemBean(SettingItemBean.ID_TOGGLE, R.mipmap.ic_power,context.getString(R.string.open_close_func)).apply {
            useSwitch = true
            switchValue = context.isFeatureOpen()
        }
        result.add(toggle)

        //反馈
        val feedback = SettingItemBean(SettingItemBean.ID_FEEDBACK,R.mipmap.ic_feedback,context.getString(R.string.feedback))
        result.add(feedback)

        //联系我们
        val contactUs = SettingItemBean(SettingItemBean.ID_CONTACT_US,R.mipmap.ic_email,context.getString(R.string.contact_us))
        result.add(contactUs)

        //隐私政策
        val privacy = SettingItemBean(SettingItemBean.ID_PRIVACY_PROTOCOL,R.mipmap.ic_privacy,context.getString(R.string.privacy_policy))
        result.add(privacy)

        //关于
        val about = SettingItemBean(SettingItemBean.ID_ABOUT,R.mipmap.ic_about,context.getString(R.string.about_us))
        result.add(about)
        return result
    }
}
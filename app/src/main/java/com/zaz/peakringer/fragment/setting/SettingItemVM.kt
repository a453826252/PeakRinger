package com.zaz.peakringer.fragment.setting

import android.content.Context
import com.zaz.peakringer.R
import com.zaz.peakringer.bean.StringItemBean
import com.zaz.peakringer.utils.autoOpenTime
import com.zaz.peakringer.utils.disableFeatureBefore
import com.zaz.peakringer.utils.disableFeatureTemporary
import com.zaz.peakringer.utils.enableFeature
import com.zaz.peakringer.utils.isFeatureOpen
import com.zaz.support.base.BaseViewModel
import com.zaz.support.utils.TimeUtils

class SettingItemVM: BaseViewModel() {
    fun getSettingItems(context: Context):MutableList<SettingItemBean>{
        val result = mutableListOf<SettingItemBean>()
        // 开关
        val titleAndSubtitle = if(context.isFeatureOpen()){
            context.getString(R.string.disable) to context.getString(R.string.enabled)
        }else{
            val autoOpenTime = context.autoOpenTime()
            if(autoOpenTime > 0){
                context.getString(R.string.enable_immediately) to context.getString(R.string.auto_enable_after,TimeUtils.getFormatTimeFromNow(context,autoOpenTime))
            }else{
                context.getString(R.string.enable) to context.getString(R.string.disabled)
            }

        }
        val toggle = SettingItemBean(SettingItemBean.ID_TOGGLE).apply {
            icon = R.mipmap.ic_power
            title = titleAndSubtitle.first
            subTitle = titleAndSubtitle.second
        }
        result.add(toggle)

        //反馈
        val feedback = SettingItemBean(SettingItemBean.ID_FEEDBACK).apply {
            icon = R.mipmap.ic_feedback
            title = context.getString(R.string.feedback)
        }
        result.add(feedback)

        //联系我们
        val contactUs = SettingItemBean(SettingItemBean.ID_CONTACT_US).apply {
            icon = R.mipmap.ic_email
            title = context.getString(R.string.contact_us)
        }
        result.add(contactUs)

        //隐私政策
        val privacy = SettingItemBean(SettingItemBean.ID_PRIVACY_PROTOCOL).apply {
            icon = R.mipmap.ic_privacy
            title = context.getString(R.string.privacy_policy)
        }
        result.add(privacy)

        //关于
        val about = SettingItemBean(SettingItemBean.ID_ABOUT).apply {
            icon = R.mipmap.ic_about
            title = context.getString(R.string.about_us)
        }
        result.add(about)
        return result
    }

    fun getCloseMenu(context: Context):List<StringItemBean>{
        val result = mutableListOf<StringItemBean>()
        result.add(StringItemBean(StringItemBean.PowerCloseType.TYPE_CLOSE_NOW,context.getString(R.string.disable)))
        result.add(StringItemBean(StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_30,context.getString(R.string.close_temp,"30${context.getString(
            com.zaz.support.R.string.time_min)}")))
        result.add(StringItemBean(StringItemBean.PowerCloseType.TYPE_CLOSE_MIN_60,context.getString(R.string.close_temp,"60${context.getString(com.zaz.support.R.string.time_min)}")))
        result.add(StringItemBean(StringItemBean.PowerCloseType.TYPE_CLOSE_HOUR_3,context.getString(R.string.close_temp,"3${context.getString(com.zaz.support.R.string.time_hour)}")))
        result.add(StringItemBean(StringItemBean.PowerCloseType.TYPE_CLOSE_CUSTOM,context.getString(R.string.other)))
        return result
    }

    fun enable(context: Context){
        context.enableFeature()
    }

    fun disableTemporary(context: Context, tempTime:Int){
        context.disableFeatureTemporary(tempTime)
    }
    fun disableBefore(context: Context,time:Long){
        context.disableFeatureBefore(time)
    }
}
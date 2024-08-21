package com.zaz.peakringer.fragment.setting

import androidx.annotation.DrawableRes

data class SettingItemBean(
    val id:Int,
    @DrawableRes
    val icon:Int,
    val title:String,
){
    var subTitle:String = ""
    var useSwitch = false   //是否在末尾使用开关代替右箭头
    var switchValue = false //当在末尾使用开关代替右箭头时，开关的值
    companion object{
        const val ID_TOGGLE = 1
        const val ID_FEEDBACK = 2
        const val ID_CONTACT_US = 3
        const val ID_PRIVACY_PROTOCOL = 4
        const val ID_ABOUT = 5
    }
}

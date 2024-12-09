package com.zaz.peakringer.fragment.setting

import androidx.annotation.DrawableRes
import com.zaz.support.Clone

data class SettingItemBean(
    val id:Int,
):Clone{
    @DrawableRes
    var icon:Int = 0
    var title:String = ""
    var subTitle:String = ""
    var subTitleClickTag = 0
    companion object{
        const val ID_TOGGLE = 1
        const val ID_FEEDBACK = 2
        const val ID_CONTACT_US = 3
        const val ID_PRIVACY_PROTOCOL = 4
        const val ID_ABOUT = 5

        const val SUBTITLE_CLICK_TAG_MODIFY_TEMP_CLOSE_TIME = 1
    }

    override fun clone(): SettingItemBean {
        return SettingItemBean(id).let {
            it.icon = icon
            it.title = title
            it.subTitle = subTitle
            it
        }
    }
}

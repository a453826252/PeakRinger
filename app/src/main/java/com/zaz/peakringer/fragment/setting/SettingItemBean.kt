package com.zaz.peakringer.fragment.setting

import androidx.annotation.DrawableRes

data class SettingItemBean(
    val id:Int,
    @DrawableRes
    val icon:Int,
    val showTxt:String,
){
    companion object{
        const val ID_TOGGLE = 1
        const val ID_FEEDBACK = 2
    }
}

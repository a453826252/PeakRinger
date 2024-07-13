package com.zaz.peakringer.fragment.contacts.edit

import com.zaz.support.dialog.bottom.IBottomItemBean

data class AvatarSelectItem(
    val type:Int,
    val contentStr:String
    ):IBottomItemBean<AvatarSelectItem> {
    override fun getContent(): String {
        return contentStr
    }
    override fun areContentsTheSame(newItem: AvatarSelectItem): Boolean {
        return type == newItem.type && contentStr == newItem.getContent()
    }
    companion object{
        const val TYPE_CAMERA = 1
        const val TYPE_GALLERY = 2
    }
}
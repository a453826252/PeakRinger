package com.zaz.support.dialog.permission

import android.view.LayoutInflater
import android.view.View
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.support.databinding.ItemPermissionBinding

class PermissionListAdapter(data:MutableList<PermissionItem>): BaseRecyclerAdapter<PermissionItem, ItemPermissionBinding>(data) {
    override fun getView(layoutInflater: LayoutInflater): ItemPermissionBinding {
        return ItemPermissionBinding.inflate(layoutInflater)
    }

    override fun bindData(position:Int, data: PermissionItem, vh: VH<ItemPermissionBinding>) {
        vh.viewBinding.itemPermissionIcon.setImageResource(data.icon)
        vh.viewBinding.itemPermissionTitle.text = data.title
        with(vh.viewBinding.itemPermissionSubtitle){
            if(data.subTitle.isNullOrBlank()){
                visibility = View.GONE
            }else{
                visibility = View.VISIBLE
                text = data.subTitle
            }
        }

    }
}
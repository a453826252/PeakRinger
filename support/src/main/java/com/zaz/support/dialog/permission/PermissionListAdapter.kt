package com.zaz.support.dialog.permission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.support.R
import com.zaz.support.databinding.ItemPermissionBinding
import com.zaz.support.utils.string

class PermissionListAdapter(data:MutableList<PermissionItem>,val authorizeBtnClick:(PermissionItem)->Unit): BaseRecyclerAdapter<PermissionItem, ItemPermissionBinding>(data) {
    override fun getView(layoutInflater: LayoutInflater,parent: ViewGroup,viewType:Int): ItemPermissionBinding {
        return ItemPermissionBinding.inflate(layoutInflater,parent,false)
    }

    override fun areContentsTheSame(oldItem: PermissionItem, newItem: PermissionItem): Boolean {
        return oldItem.permission == newItem.permission && oldItem.title == newItem.title && oldItem.granted == newItem.granted
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
        if(data.granted){
            vh.viewBinding.itemPermissionAuthorize.setOnClickListener(null)
            vh.viewBinding.itemPermissionAuthorize.isEnabled = false
            vh.viewBinding.itemPermissionAuthorize.text = R.string.authorize_already.string(vh.viewBinding.root.context)
        }else{
            vh.viewBinding.itemPermissionAuthorize.isEnabled = true
            vh.viewBinding.itemPermissionAuthorize.text = R.string.authorize.string(vh.viewBinding.root.context)
            vh.viewBinding.itemPermissionAuthorize.setOnClickListener{
                authorizeBtnClick(data)
            }
        }
    }
}
package com.zaz.support.dialog.bottom

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.support.databinding.ItemTextviewBinding

internal class BottomItemAdapter<T:IBottomItemBean<T>>(data:MutableList<T>, val onItemClick:((T)->Unit)?=null): BaseRecyclerAdapter<T, ItemTextviewBinding>(data) {
    companion object{
        const val TAG = "PermissionListAdapter"
    }
    override fun getView(layoutInflater: LayoutInflater,parent: ViewGroup,viewType:Int): ItemTextviewBinding {
        return ItemTextviewBinding.inflate(layoutInflater,parent,false)
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

    override fun bindData(position:Int, data: T, vh: VH<ItemTextviewBinding>) {
        vh.viewBinding.itemTextviewContent.text = data.getContent()
        onItemClick?.let {
            vh.viewBinding.root.setOnClickListener {
                it(data)
            }
        }
    }
}
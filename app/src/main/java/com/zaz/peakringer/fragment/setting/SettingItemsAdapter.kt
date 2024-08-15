package com.zaz.peakringer.fragment.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.peakringer.databinding.ItemSettingBinding

class SettingItemsAdapter(data: MutableList<SettingItemBean>,val onItemClick:(SettingItemBean)->Unit): BaseRecyclerAdapter<SettingItemBean, ItemSettingBinding>(data),
    View.OnClickListener {
    override fun getView(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSettingBinding {
        return ItemSettingBinding.inflate(layoutInflater,parent,false)
    }

    override fun bindData(position: Int, data: SettingItemBean, vh: VH<ItemSettingBinding>) {
        vh.viewBinding.itemSettingIcon.setImageResource(data.icon)
        vh.viewBinding.itemSettingName.text = data.showTxt
        vh.itemView.tag = data
        vh.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        (v.tag as? SettingItemBean)?.let {
            onItemClick(it)
        }
    }
}
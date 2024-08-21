package com.zaz.peakringer.fragment.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaz.peakringer.R
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.peakringer.databinding.ItemSettingBinding

class SettingItemsAdapter(data: MutableList<SettingItemBean>,val onItemClick:(Int,SettingItemBean)->Unit): BaseRecyclerAdapter<SettingItemBean, ItemSettingBinding>(data),
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
        vh.viewBinding.itemSettingTitle.text = data.title
        vh.viewBinding.itemSettingSubtitle.text = data.subTitle
        if(data.useSwitch){
            vh.viewBinding.itemSettingEndIcon.visibility = View.GONE
            with(vh.viewBinding.itemSettingSwitch){
                visibility = View.VISIBLE
                isChecked = data.switchValue
            }
        }else{
            vh.viewBinding.itemSettingEndIcon.visibility = View.VISIBLE
            vh.viewBinding.itemSettingSwitch.visibility = View.GONE
        }
        vh.itemView.setTag(R.id.item_setting_data,data)
        vh.itemView.setTag(R.id.item_setting_position,position)
        vh.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        (v.getTag(R.id.item_setting_data) as? SettingItemBean)?.let {
            val position = v.getTag(R.id.item_setting_position) as Int
            onItemClick(position,it)
        }
    }
}
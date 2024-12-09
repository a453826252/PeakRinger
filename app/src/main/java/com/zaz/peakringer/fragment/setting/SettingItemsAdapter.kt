package com.zaz.peakringer.fragment.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaz.peakringer.R
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.peakringer.databinding.ItemSettingBinding

class SettingItemsAdapter(data: MutableList<SettingItemBean>,val onItemClick:(Int,SettingItemBean)->Unit,val onSubtitleClick:(Int,SettingItemBean)->Unit): BaseRecyclerAdapter<SettingItemBean, ItemSettingBinding>(data),
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
        vh.itemView.setTag(R.id.item_setting_data,data)
        vh.itemView.setTag(R.id.item_setting_position,position)
        vh.itemView.setOnClickListener(this)
        if(data.subTitleClickTag != 0){
            vh.viewBinding.itemSettingSubtitle.setOnClickListener{
                onSubtitleClick(position,data)
            }
        }else{
            vh.viewBinding.itemSettingSubtitle.setOnClickListener(null)
        }
    }

    override fun onClick(v: View) {
        (v.getTag(R.id.item_setting_data) as? SettingItemBean)?.let {
            val position = v.getTag(R.id.item_setting_position) as Int
            onItemClick(position,it)
        }
    }

    fun getItem(id:Int):SettingItemBean?{
        for (d in getData()){
            if(d.id == id){
                return d
            }
        }
        return null
    }
    fun notifyItemChanged(item:SettingItemBean){
        for ((index,d) in dataList.withIndex()){
            if(d.id == item.id){
                d.icon = item.icon
                d.title = item.title
                d.subTitle = item.subTitle
                notifyItemChanged(index)
                break
            }
        }
    }
}
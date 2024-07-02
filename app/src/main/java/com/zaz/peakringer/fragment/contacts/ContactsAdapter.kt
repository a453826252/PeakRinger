package com.zaz.peakringer.fragment.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.peakringer.databinding.ItemContactsBinding

class ContactsAdapter: BaseRecyclerAdapter<ContactsBean, ItemContactsBinding>() {
    override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemContactsBinding {
        return ItemContactsBinding.inflate(layoutInflater,parent,false)
    }

    override fun bindData(position: Int, data: ContactsBean, vh: VH<ItemContactsBinding>) {
        vh.viewBinding.itemContactName.text = data.name
        vh.viewBinding.itemContactNumber.text = data.phoneNumber
    }

    override fun areContentsTheSame(oldItem: ContactsBean, newItem: ContactsBean): Boolean {
        return oldItem.phoneNumber == newItem.phoneNumber && oldItem.name == newItem.name
    }

}
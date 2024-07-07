package com.zaz.peakringer.fragment.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.zaz.peakringer.R
import com.zaz.peakringer.base.BaseRecyclerAdapter
import com.zaz.peakringer.databinding.ItemContactsBinding
import java.io.File

class ContactsAdapter(val editContactCallback:(Int,ContactsBean)->Unit): BaseRecyclerAdapter<ContactsBean, ItemContactsBinding>(), View.OnClickListener {
    companion object{
        const val EDIT_TYPE_EDIT = 1
        const val EDIT_TYPE_DEL = 2
    }
    override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemContactsBinding {
        return ItemContactsBinding.inflate(layoutInflater,parent,false)
    }

    override fun bindData(position: Int, data: ContactsBean, vh: VH<ItemContactsBinding>) {
        vh.viewBinding.itemContactName.text = data.name
        vh.viewBinding.itemContactNumber.text = data.displayPhoneNumber
        vh.viewBinding.itemContactEditBtn.let {
            it.tag = data
            it.setOnClickListener(this)
        }
        vh.viewBinding.itemContactDelBtn.let {
            it.tag = data
            it.setOnClickListener(this)
        }
        if(!data.icon.isNullOrBlank()){
            if(data.icon.startsWith("/")){
                Glide.with(vh.itemView.context)
                    .load(File(data.icon))
                    .error(R.mipmap.ic_default_avatar)
                    .placeholder(R.mipmap.ic_default_avatar)
                    .into(vh.viewBinding.itemContactProfilePhoto)
            }
        }

    }

    override fun areContentsTheSame(oldItem: ContactsBean, newItem: ContactsBean): Boolean {
        return oldItem.phoneNumber == newItem.phoneNumber && oldItem.name == newItem.name
    }

    override fun onClick(v: View) {
        if(v.id == R.id.item_contact_edit_btn){
            editContactCallback(EDIT_TYPE_EDIT,v.tag as ContactsBean)
        }else if(v.id == R.id.item_contact_del_btn){
            editContactCallback(EDIT_TYPE_DEL,v.tag as ContactsBean)
        }
    }
}
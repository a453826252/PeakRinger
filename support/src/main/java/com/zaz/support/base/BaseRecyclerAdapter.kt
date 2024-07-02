package com.zaz.peakringer.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerAdapter<T,R:ViewBinding>(private val dataList:MutableList<T> = mutableListOf()): RecyclerView.Adapter<BaseRecyclerAdapter.VH<R>>() {
    val data:MutableList<T>
        get() = mutableListOf<T>().apply {
        addAll(dataList)
    }
    abstract fun getView(layoutInflater: LayoutInflater,parent: ViewGroup,viewType: Int):R
    abstract fun bindData(position: Int,data:T,vh:VH<R>)

    open fun areItemsTheSame(oldItem: T, newItem: T):Boolean = true
    open fun areContentsTheSame(oldItem: T, newItem: T):Boolean = false
    fun submitData(data:List<T>){
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = dataList.size

            override fun getNewListSize() = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(dataList[oldItemPosition],data[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areContentsTheSame(dataList[oldItemPosition],data[newItemPosition])
            }

        }).apply { dataList.clear();dataList.addAll(data) }.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<R> {
        return VH(getView(LayoutInflater.from(parent.context),parent,viewType))
    }

    override fun getItemCount(): Int  = dataList.size

    override fun onBindViewHolder(holder: VH<R>, position: Int) {
        bindData(position,dataList[position],holder)
    }

    class VH<R:ViewBinding>(val viewBinding: R): RecyclerView.ViewHolder(viewBinding.root) {

    }
}
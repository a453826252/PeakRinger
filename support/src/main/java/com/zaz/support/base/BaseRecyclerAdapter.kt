package com.zaz.peakringer.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerAdapter<T,R:ViewBinding>(private val dataList:MutableList<T> = mutableListOf()): RecyclerView.Adapter<BaseRecyclerAdapter.VH<R>>() {
    abstract fun getView(layoutInflater: LayoutInflater):R
    abstract fun bindData(position: Int,data:T,vh:VH<R>)
    fun submitData(data:List<T>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<R> {
        return VH(getView(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int  = dataList.size

    override fun onBindViewHolder(holder: VH<R>, position: Int) {
        bindData(position,dataList[position],holder)
    }

    class VH<R:ViewBinding>(val viewBinding: R): RecyclerView.ViewHolder(viewBinding.root) {

    }
}
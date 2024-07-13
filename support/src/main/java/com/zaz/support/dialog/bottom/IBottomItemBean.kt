package com.zaz.support.dialog.bottom

interface IBottomItemBean<T> {
    fun areItemsTheSame(newItem: T):Boolean = true
    fun areContentsTheSame( newItem: T):Boolean = false

    fun getContent():String
}
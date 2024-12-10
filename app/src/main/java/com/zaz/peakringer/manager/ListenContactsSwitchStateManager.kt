package com.zaz.peakringer.manager

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

object ListenContactsSwitchStateManager {
    private val stateLiveData = MutableLiveData<ListenContactsSwitchState>()

    fun onStateChanged(isOpen: Boolean){
        onStateChanged(isOpen,-1)
    }
    fun onStateChanged(isOpen:Boolean, autoOpenAt:Long){
        stateLiveData.postValue(ListenContactsSwitchState(isOpen, autoOpenAt))
    }

    fun addObserver(owner: LifecycleOwner, observer: Observer<ListenContactsSwitchState>){
        stateLiveData.observe(owner,observer)
    }

    fun removeObserver(observer: Observer<ListenContactsSwitchState>){
        stateLiveData.removeObserver(observer)
    }
}

data class ListenContactsSwitchState(val isOpen:Boolean,val autoOpenAt:Long)
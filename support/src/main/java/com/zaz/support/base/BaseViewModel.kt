package com.zaz.support.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel() : ViewModel() {
    protected val _showLoading = MutableLiveData<String?>()
    val showLoading: LiveData<String?> = _showLoading

    protected val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast
}
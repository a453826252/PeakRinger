package com.zaz.support.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.zaz.support.dialog.PRLoading
import com.zaz.support.utils.PRToast

abstract class BaseActivity: FragmentActivity() {
    protected lateinit var loading: PRLoading
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = PRLoading()
        getBaseViewModel()?.let { base->
            with(base){
                toast.observe(this@BaseActivity){
                    PRToast.show(this@BaseActivity,it)
                }
                showLoading.observe(this@BaseActivity){
                    if(it == null){
                        loading.dismiss()
                    }else{
                        loading.show(it,supportFragmentManager)
                    }
                }
            }
        }
    }
    abstract fun getBaseViewModel():BaseViewModel?
}
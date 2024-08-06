package com.zaz.support.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zaz.support.dialog.PRLoading
import com.zaz.support.utils.PRToast

abstract class BaseFragment: Fragment() {
    protected lateinit var loading: PRLoading
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = PRLoading()
        getBaseViewModel()?.let { base->
            with(base){
                toast.observe(viewLifecycleOwner){
                    PRToast.show(view.context,it)
                }
                showLoading.observe(viewLifecycleOwner){
                    if(it == null){
                        loading.dismiss()
                    }else{
                        loading.show(it,childFragmentManager)
                    }
                }
            }
        }

    }

    abstract fun getBaseViewModel():BaseViewModel?
}
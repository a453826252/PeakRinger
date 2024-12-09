package com.zaz.support.base

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.zaz.support.R
import com.zaz.support.dialog.PRLoading
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.finishFragment

abstract class BaseFragment: Fragment() {
    protected lateinit var loading: PRLoading
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            onBackPressed()
        }
    }
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = PRLoading()
        getBaseViewModel()?.let { base->
            with(base){
                toast.observe(viewLifecycleOwner){
                    PRToast.show(view.context.applicationContext,it)
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

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if(transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN){
            if(enter){
                return AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_right_in)
            }else{
                return AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_right_out)
            }
        }else if(transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE){
            if(enter){
                return AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_right_in)
            }else{
                return AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_right_out)
            }
        }
        return null
    }



    abstract fun getBaseViewModel():BaseViewModel?
    open fun onBackPressed(){
        finishFragment()
    }
    companion object{
        fun show(fm: FragmentManager,fragment: Fragment, container:Int, tag:String){
            fm.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack(null)
                add(container,fragment,tag)
            }
        }
    }
}
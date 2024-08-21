package com.zaz.peakringer.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zaz.peakringer.activity.CommonActivity
import com.zaz.support.utils.SpUtils

fun Fragment.startFragment(type: Int,data:Bundle?=null) {
    startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
        putExtra(CommonActivity.FRAGMENT_TYPE_KEY, type)
        data?.let {
            putExtra("extra",it)
        }
    })
}

fun Context.isFeatureOpen() = SpUtils.getPrConfigInstance(this).get(SpUtils.FEATURE_OPEN,true)
fun Context.changeFeatureOpen(open:Boolean) = SpUtils.getPrConfigInstance(this).put(SpUtils.FEATURE_OPEN,open)
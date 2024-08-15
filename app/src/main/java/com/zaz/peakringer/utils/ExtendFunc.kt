package com.zaz.peakringer.utils

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zaz.peakringer.activity.CommonActivity

fun Fragment.startFragment(type: Int,data:Bundle?=null) {
    startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
        putExtra(CommonActivity.FRAGMENT_TYPE_KEY, type)
        data?.let {
            putExtra("extra",it)
        }
    })
}
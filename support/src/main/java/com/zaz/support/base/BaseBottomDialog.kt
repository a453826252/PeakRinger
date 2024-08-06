package com.zaz.support.base

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaz.support.R

open class BaseBottomDialog: BottomSheetDialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.run {
            // 取消默认的背景色
            try {
                // hack bg color of the BottomSheetDialog
                val parent = view?.parent as View
                parent?.setBackgroundResource(R.color.transparent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
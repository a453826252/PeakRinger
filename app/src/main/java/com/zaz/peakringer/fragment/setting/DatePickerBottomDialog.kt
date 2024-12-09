package com.zaz.peakringer.fragment.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.zaz.peakringer.R
import com.zaz.peakringer.databinding.DialogBottomDatePickerBinding
import com.zaz.support.base.BaseBottomDialog
import com.zaz.support.utils.color
import java.text.SimpleDateFormat

class DatePickerBottomDialog:BaseBottomDialog() {
    companion object{
        @JvmStatic
        fun  show(
            fm: FragmentManager,
            onItemClickCallback: (Long) -> Unit
        ): DatePickerBottomDialog {
            val dialog = DatePickerBottomDialog()
            dialog.onTimeSelected = onItemClickCallback
            dialog.show(fm,"DatePickerBottomDialog")
            return dialog
        }
    }
    private lateinit var viewBinding:DialogBottomDatePickerBinding
    private var onTimeSelected:((Long)->Unit)? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogBottomDatePickerBinding.inflate(inflater).let {
            viewBinding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding.timePicker) {
            setThemeColor(com.zaz.support.R.color.main_color.color(requireContext()))
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            setOnDateTimeChangedListener {
                val timeFormat = format.format(it)
                viewBinding.timePickerResult.text = getString(R.string.auto_enable_after, timeFormat)
            }
        }
        viewBinding.timePickerSelectBtn.setOnClickListener {
            onTimeSelected?.invoke(viewBinding.timePicker.getMillisecond() / 1000)
            dismiss()
        }
    }
}
package com.zaz.support.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.zaz.support.databinding.DialogLoadingBinding

class PRLoading: DialogFragment() {
    private lateinit var binding: DialogLoadingBinding
    private var msg:String?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!msg.isNullOrBlank()){
            binding.dialogLoadingMsg.text = msg
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val attr = it.attributes
            attr.width = WindowManager.LayoutParams.WRAP_CONTENT
            attr.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.attributes = attr
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun show(msg:String?,fm:FragmentManager){
        this.msg = msg
        show(fm,"loadingDialog")
    }
}
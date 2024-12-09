package com.zaz.support.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.zaz.support.R
import com.zaz.support.databinding.DialogPrBinding
import com.zaz.support.utils.dp

class PRDialog private constructor(): DialogFragment() {
    private lateinit var binding: DialogPrBinding
    private var config: Config?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPrBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = arguments?.getParcelable("config")
        if (config == null){
            Log.e(TAG, "onViewCreated: config is null", Throwable())
            return
        }
        config?.let {
            if(it.title.isNullOrBlank()){
                binding.prDialogTitle.visibility = View.GONE
            }else{
                with(binding.prDialogTitle) {
                    visibility = View.VISIBLE
                    text=it.title
                }
            }

            binding.prDialogContent.text= if(it.content.isNullOrBlank()){
                getString(R.string.err_pr_content)
            }else{
                it.content
            }

            if(it.leftBtnName.isNullOrBlank()){
                binding.prDialogCancelButton.visibility = View.GONE
                binding.prDialogBtnSpace.visibility = View.GONE
            }else{
                with(binding.prDialogCancelButton){
                    text = it.leftBtnName
                    visibility = View.VISIBLE
                    setOnClickListener { _->
                        dismiss()
                        it.leftListener?.invoke(this@PRDialog)
                    }

                }
            }
            if(it.rightBtnName.isNullOrBlank()){
                binding.prDialogConfirmButton.visibility = View.GONE
                binding.prDialogBtnSpace.visibility = View.GONE
            }else{
                with(binding.prDialogConfirmButton){
                    text = it.rightBtnName
                    visibility = View.VISIBLE
                    setOnClickListener { _->
                        dismiss()
                        it.rightListener?.invoke(this@PRDialog)
                    }
                }
            }
            it.hideNotShowBtn.let {
                binding.prDialogNotShowAgain.visibility = if(it){
                    View.GONE
                }else{
                    View.VISIBLE
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val attr = it.attributes
            attr.width = (resources.displayMetrics.widthPixels - 72.dp).toInt()
            it.attributes = attr
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.let {
            it.setCancelable(config?.cancelable ?: true)
            it.setCanceledOnTouchOutside(config?.canceledOnTouchOutside ?: true)
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config?.dismissListener?.invoke(this)
    }

    fun dontShowAgain() = binding.prDialogNotShowAgain.isChecked

    companion object{
        const val TAG = "PRDialog"
        @JvmStatic
        private fun newInstance(config: Config): PRDialog {
            return PRDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("config",config)
                }
            }
        }
    }

    class Builder{
        private val config = Config()

        fun setTitle(title:String): Builder {
            config.title = title
            return this
        }
        fun setContent(content:String): Builder {
            config.content = content
            return this
        }
        fun setLeftBtnName(left:String): Builder {
            config.leftBtnName = left
            return this
        }
        fun setRightBtnName(right:String): Builder {
            config.rightBtnName = right
            return this
        }
        fun setLeftBtnListener(leftListener:(PRDialog)->Unit): Builder {
            config.leftListener =leftListener
            return this
        }
        fun setRightBtnListener(rightListener:(PRDialog)->Unit): Builder {
            config.rightListener =rightListener
            return this
        }
        fun setDismissListener(dismissListener:(PRDialog)->Unit): Builder {
            config.dismissListener =dismissListener
            return this
        }
        fun setHideNotShowBtn(notShow:Boolean): Builder {
            config.hideNotShowBtn = notShow
            return this
        }
        fun setCancelable(cancelable:Boolean): Builder {
            config.cancelable = cancelable
            return this
        }
        fun setCanceledOnTouchOutside(canceledOnTouchOutside:Boolean): Builder {
            config.canceledOnTouchOutside = canceledOnTouchOutside
            return this
        }
        fun show(fragmentManager: FragmentManager): PRDialog {
            val dialog = newInstance(config)
            fragmentManager.commit { add(dialog,"") }
            return dialog
        }


    }
    class Config() :Parcelable{
        var title:String? = ""
        var content:String? = ""
        var leftBtnName:String?=""
        var rightBtnName:String? = ""
        var hideNotShowBtn:Boolean=true
        var leftListener:((PRDialog)->Unit)?=null
        var rightListener:((PRDialog)->Unit)?=null
        var dismissListener:((PRDialog)->Unit)?=null
        var cancelable = true
        var canceledOnTouchOutside = true
        constructor(parcel: Parcel) : this() {
            title = parcel.readString()
            content = parcel.readString()
            leftBtnName = parcel.readString()
            rightBtnName = parcel.readString()
            hideNotShowBtn = parcel.readInt() == 1
            cancelable = parcel.readInt() == 1
            canceledOnTouchOutside = parcel.readInt() == 1
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(content)
            parcel.writeString(leftBtnName)
            parcel.writeString(rightBtnName)
            parcel.writeInt(if(hideNotShowBtn) 1 else 0)
            parcel.writeInt(if(cancelable) 1 else 0)
            parcel.writeInt(if(canceledOnTouchOutside) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Config> {
            override fun createFromParcel(parcel: Parcel): Config {
                return Config(parcel)
            }

            override fun newArray(size: Int): Array<Config?> {
                return arrayOfNulls(size)
            }
        }
    }
}
package com.zaz.support.dialog.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zaz.support.R
import com.zaz.support.databinding.DialogBottomItemBinding
import com.zaz.support.dercoration.VerticalDecoration
import com.zaz.support.utils.color

class BottomItemDialog<T : IBottomItemBean<T>> : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "BottomItemDialog"

        @JvmStatic
        fun <T : IBottomItemBean<T>> show(
            fm: FragmentManager,
            items: List<T>,
            onItemClickCallback: (T) -> Unit
        ): BottomItemDialog<T> {
            val dialog = BottomItemDialog<T>()
            dialog.onItemClickCallback = onItemClickCallback
            dialog.addItems(items)
            fm.commit { add(dialog, TAG) }
            return dialog
        }
    }

    private lateinit var viewBinding: DialogBottomItemBinding
    private var onItemClickCallback: ((T) -> Unit)? = null
    private lateinit var adapter: BottomItemAdapter<T>
    private var items:MutableList<T> = mutableListOf()
    private fun addItems(itemBeans: List<T>){
        items.clear()
        items.addAll(itemBeans)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DialogBottomItemBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BottomItemAdapter(items, ::onItemClick)
        viewBinding.dialogBottomItems.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.dialogBottomItems.adapter = adapter
        viewBinding.dialogBottomItems.addItemDecoration(
            VerticalDecoration(
                R.color.border_of_cancel_btn.color(
                    requireContext()
                ), 1f
            )
        )
        viewBinding.dialogPermissionCancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onItemClick(data:T){
        dismiss()
        onItemClickCallback?.invoke(data)
    }
}
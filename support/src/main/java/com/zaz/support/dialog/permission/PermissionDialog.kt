package com.zaz.support.dialog.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaz.support.databinding.DialogPermissionBinding

class PermissionDialog private constructor(): DialogFragment() {
    private lateinit var viewBinding: DialogPermissionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DialogPermissionBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissions = arguments?.getParcelableArrayList<PermissionItem>("permissions")
        checkPermissionArgument(permissions)
        val adapter = PermissionListAdapter(permissions!!)
        viewBinding.permissionList.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.permissionList.adapter = adapter
    }

    private fun checkPermissionArgument(permissions:List<PermissionItem>?){
        if(permissions.isNullOrEmpty()){
            throw IllegalArgumentException("$TAG:permission array is null or empty")
        }

        for (per in permissions){
            if(per.permission.isBlank() || per.title.isBlank()){
                throw IllegalArgumentException("$TAG: permission is empty,permission=${per.permission},title=${per.title}")
            }
            if(per.icon == 0){
                throw IllegalArgumentException("$TAG: permission iconRes is empty,title=${per.title}")
            }
        }
    }

    companion object{
        const val TAG = "PermissionDialog"
        @JvmStatic
        fun show(fm:FragmentManager,permissions:ArrayList<PermissionItem>): PermissionDialog {
            val dialog = PermissionDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions",permissions)
                }
            }
            fm.commit { add(dialog, TAG) }
            return dialog
        }
    }
}
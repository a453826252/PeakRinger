package com.zaz.peakringer.dialog

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.activity.main.MainActivity
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.isPermissionGranted

class LaunchPermissionDialog:PermissionDialog() {
    private var onCancelListener:(((LaunchPermissionDialog)->Unit))?=null
    override fun checkIfGranted(permissionItem: PermissionItem): Boolean {
        return when (permissionItem.permission) {
            ROLE_PERMISSION -> {
                CallScreenRoleManager.isRoleHeld()
            }
            READ_PHONE_STATE -> {
                requireContext().isPermissionGranted(android.Manifest.permission.READ_PHONE_STATE)
            }
            else -> {
                false
            }
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCancel() {
        onCancelListener?.invoke(this)
    }
    override fun onAuthorizeBtnClick(permissionItem: PermissionItem) {
        when (permissionItem.permission) {
            ROLE_PERMISSION -> {
                CallScreenRoleManager.requestRole(requireActivity())
            }

            READ_PHONE_STATE -> {
                requireActivity().requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE),MainActivity.REQUEST_PERMISSION_READ_PHONE_STATE)
            }
        }
    }

    companion object{
        const val ROLE_PERMISSION = "role_manager_permission"
        const val READ_PHONE_STATE = "read_phone_state"
        fun show(activity: FragmentActivity, permissions:ArrayList<PermissionItem>,onCancel:(LaunchPermissionDialog)->Unit){
            val dialog = LaunchPermissionDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions",permissions)
                }
            }
            dialog.onCancelListener = onCancel
            dialog.show(activity.supportFragmentManager,TAG)
        }
    }
}
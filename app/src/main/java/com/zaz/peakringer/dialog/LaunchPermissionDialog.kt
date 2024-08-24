package com.zaz.peakringer.dialog

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.activity.main.MainActivity
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.isPermissionGranted
import kotlinx.coroutines.launch

class LaunchPermissionDialog(private val activity: FragmentActivity):PermissionDialog() {
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
        fun show(activity: FragmentActivity, permissions:ArrayList<PermissionItem>){
            val dialog = LaunchPermissionDialog(activity).apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("permissions",permissions)
                }
            }
            dialog.show(activity.supportFragmentManager,TAG)
        }
    }
}
package com.zaz.peakringer.dialog

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.support.dialog.permission.PermissionDialog
import com.zaz.support.dialog.permission.PermissionItem

class LaunchPermissionDialog(private val activity: FragmentActivity):PermissionDialog() {
    override fun checkIfGranted(permissionItem: PermissionItem): Boolean {
        return when (permissionItem.permission) {
            ROLE_PERMISSION -> {
                CallScreenRoleManager.isRoleHeld()
            }
            NOTIFICATION_PERMISSION -> {
                NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            }
            else -> {
                false
            }
        }
    }

    override fun onAuthorizeBtnClick(permissionItem: PermissionItem) {
        when (permissionItem.permission) {
            ROLE_PERMISSION -> {
                CallScreenRoleManager.checkAndRequestRole(activity)
            }

            NOTIFICATION_PERMISSION -> {
                startActivity(Intent().apply {
                    setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    putExtra(Settings.EXTRA_APP_PACKAGE,activity.packageName)
                })
            }
        }
    }

    companion object{
        const val ROLE_PERMISSION = "role_manager_permission"
        const val NOTIFICATION_PERMISSION = "notification_permission"
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
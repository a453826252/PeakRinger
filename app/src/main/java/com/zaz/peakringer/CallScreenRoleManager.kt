package com.zaz.peakringer

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

object CallScreenRoleManager {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val roleManager: RoleManager = PRApp.application.getSystemService(Context.ROLE_SERVICE) as RoleManager
    private const val ROLE = RoleManager.ROLE_CALL_SCREENING
    fun register(activity: FragmentActivity, result: ((ActivityResult) -> Unit)? = null) {
        val activityResult = result ?: {
            if (it.resultCode != Activity.RESULT_OK) {
                com.zaz.support.utils.PRToast.show(
                    activity,
                    activity.getString(R.string.request_call_screen_permission)
                )
            }
        }
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            activityResult
        )
    }

    fun requestRole(activity: FragmentActivity){
        activity.lifecycleScope.launch {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val intent = roleManager.createRequestRoleIntent(ROLE)
                launcher.launch(intent)
            }
        }
    }
    fun isRoleHeld() = roleManager.isRoleHeld(ROLE)
    fun isRoleAvailable() = roleManager.isRoleAvailable(ROLE)
}
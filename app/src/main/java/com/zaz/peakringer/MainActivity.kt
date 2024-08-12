package com.zaz.peakringer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.dialog.LaunchPermissionDialog
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment
import com.zaz.peakringer.fragment.feedback.FeedbackFragment
import com.zaz.peakringer.utils.UpdateUtils
import com.zaz.support.base.BaseActivity
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.update.StartUpdateConfig
import com.zaz.update.UpdateCheckResult
import com.zaz.update.UpdateDownloadState

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var gpUpdate:ActivityResultLauncher<IntentSenderRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CallScreenRoleManager.register(this){
            if (it.resultCode != Activity.RESULT_OK) {
                PRToast.show(
                    this,
                    getString(R.string.request_call_screen_permission)
                )
            }
        }
        val installNewVer = intent.getBooleanExtra("installNewVer",false)
        Log.d(TAG, "onCreate: checkUpdate=$installNewVer")
        gpUpdate = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            Log.d(TAG, "GP flex update: resultCode=${it.resultCode}")
            if(it.resultCode != FragmentActivity.RESULT_OK){
                // If the update is canceled or fails,
                // you can request to start the update again.
            }
        }
        if(installNewVer){
            UpdateUtils.install(this)
        }else{
            checkUpdate()
        }
    }

    private fun checkUpdate(){
        UpdateUtils.checkUpdate(this){
            Log.d(TAG, "checkUpdate: result=$it")
            if(it == UpdateCheckResult.NO_UPDATE){
                if(!CallScreenRoleManager.isRoleHeld()){
                    val permissions = ArrayList<PermissionItem>()
                    permissions.add(
                        PermissionItem(
                            LaunchPermissionDialog.ROLE_PERMISSION,
                            getString(R.string.default_caller_id),
                            R.mipmap.ic_phone_blue,
                        )
                    )
                    LaunchPermissionDialog.show(this, permissions)
                }
            }else{
                UpdateUtils.startUpdate(it, StartUpdateConfig().apply {
                    this.context = this@MainActivity
                    callback = ::onUpdateDownloadCallback
                    googleUpdateLauncher = gpUpdate
                })
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val installNewVer = intent?.getBooleanExtra("installNewVer",false)
        Log.d(TAG, "onNewIntent: installNewVer=$installNewVer")
        if(installNewVer == true){
            UpdateUtils.install(this)
        }else{
            checkUpdate()
        }
    }


    fun showEditOrAddFragment(contact:ContactsBean?){
        EditOrAddContactFragment.show(supportFragmentManager,binding.root.id,contact)
    }

    fun showFeedback(){
        FeedbackFragment.show(supportFragmentManager,binding.root.id)
    }


    private fun onUpdateDownloadCallback(checkResult: UpdateCheckResult, downloadState: UpdateDownloadState, byteDownload:Long, byteNeedDown:Long){
        UpdateUtils.showDownloadedNotification(this,downloadState,byteDownload,byteNeedDown){
            if(it == UpdateDownloadState.SUCCEED){
                UpdateUtils.install(this)
            }else{
                checkUpdate()
            }
        }
    }
}
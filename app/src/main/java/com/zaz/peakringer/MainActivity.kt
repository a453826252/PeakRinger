package com.zaz.peakringer

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.zaz.google.AppUpdate
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment
import com.zaz.peakringer.fragment.feedback.FeedbackFragment
import com.zaz.peakringer.utils.UpdateUtils
import com.zaz.support.base.BaseActivity
import com.zaz.support.dialog.PRDialog

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UpdateUtils.checkUpdate(this)
        val checkUpdate = intent.getBooleanExtra("checkUpdate",false)
        Log.d(TAG, "onCreate: checkUpdate=$checkUpdate")
        checkAndRequestNotificationPermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val checkUpdate = intent?.getBooleanExtra("checkUpdate",false)
        Log.d(TAG, "onNewIntent: checkUpdate=$checkUpdate")
    }


    fun showEditOrAddFragment(contact:ContactsBean?){
        EditOrAddContactFragment.show(supportFragmentManager,binding.root.id,contact)
    }

    fun showFeedback(){
        FeedbackFragment.show(supportFragmentManager,binding.root.id)
    }

    private fun onAppUpdateAvailability(type:Int){
        if(type == AppUpdate.APP_UPDATE_IMMEDIATE){

        }else{

        }
    }

    private fun checkAndRequestNotificationPermission(){
        val notificationManager = NotificationManagerCompat.from(this)
        if(!notificationManager.areNotificationsEnabled()){
            Log.d(TAG, "checkAndRequestNotificationPermission: no notification permission")
            PRDialog.Builder()
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.open_notification_permission_content))
                .setLeftBtnName(getString(R.string.cancel))
                .setLeftBtnListener {
                    it.dismiss()
                }
                .setRightBtnName(getString(com.zaz.support.R.string.yes))
                .setRightBtnListener {
                    it.dismiss()
                    startActivity(Intent().apply {
                        setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        putExtra(Settings.EXTRA_APP_PACKAGE,packageName)
                    })
                }
                .show(supportFragmentManager)
        }
    }

}
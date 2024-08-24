package com.zaz.peakringer.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.Constant
import com.zaz.peakringer.R
import com.zaz.peakringer.bean.ImgTxtBean
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.databinding.TabMainActivityBottomBinding
import com.zaz.peakringer.dialog.LaunchPermissionDialog
import com.zaz.peakringer.fragment.contacts.display.ContactsFragment
import com.zaz.peakringer.fragment.setting.SettingFragment
import com.zaz.peakringer.receiver.StaticsBroadcast
import com.zaz.peakringer.utils.UpdateUtils
import com.zaz.support.base.BaseActivity
import com.zaz.support.dialog.PRDialog
import com.zaz.support.dialog.permission.PermissionItem
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.goToAppDetails
import com.zaz.support.utils.isPermissionGranted
import com.zaz.update.StartUpdateConfig
import com.zaz.update.UpdateCheckResult
import com.zaz.update.UpdateDownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    companion object{
        private const val TAG = "MainActivity"
        const val REQUEST_PERMISSION_READ_PHONE_STATE = 1001
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var gpUpdate:ActivityResultLauncher<IntentSenderRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CallScreenRoleManager.register(this) {
            if (it.resultCode != RESULT_OK) {
                PRToast.show(
                    this,
                    getString(R.string.request_call_screen_permission)
                )
            }else{
                proceedIntent()
            }
        }
        initView()
        gpUpdate = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            Log.d(TAG, "GP flex update: resultCode=${it.resultCode}")
            if(it.resultCode != FragmentActivity.RESULT_OK){
                // If the update is canceled or fails,
                // you can request to start the update again.
            }
        }
        proceedIntent()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSION_READ_PHONE_STATE){
            if(permissions.isEmpty() || !isPermissionGranted(permissions[0])){
                PRDialog.Builder()
                    .setTitle(getString(R.string.important))
                    .setContent(getString(R.string.no_read_phone_state_permission))
                    .setLeftBtnName(getString(R.string.i_see))
                    .setRightBtnName(getString(com.zaz.support.R.string.authorize))
                    .setRightBtnListener {
                        goToAppDetails()
                    }
                    .show(supportFragmentManager)
            }else{
                proceedIntent()
            }
        }
    }

    private fun initView(){
        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(ContactsFragment())
        fragmentList.add(SettingFragment())
        val adapter = LaunchFragmentAdapter(this,fragmentList)
        binding.viewPage.adapter = adapter
        val tabList = mutableListOf<ImgTxtBean>()
        tabList.add(ImgTxtBean(R.mipmap.ic_contacts,getString(R.string.title_contacts)))
        tabList.add(ImgTxtBean(R.mipmap.ic_setting,getString(R.string.action_settings)))

        binding.bottomTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    it.customView?.findViewById<View>(R.id.tab_icon)?.isSelected = true
                    it.customView?.findViewById<View>(R.id.tab_txt)?.isSelected = true
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    it.customView?.findViewById<View>(R.id.tab_icon)?.isSelected = false
                    it.customView?.findViewById<View>(R.id.tab_txt)?.isSelected = false
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })
        TabLayoutMediator(binding.bottomTab,binding.viewPage){ tab, position ->
            val view = TabMainActivityBottomBinding.inflate(layoutInflater)
            tab.setCustomView(view.root)
            view.tabTxt.text = tabList[position].txt
            view.tabIcon.setImageResource(tabList[position].img)
        }.attach()
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val installNewVer = intent?.getBooleanExtra("installNewVer",false)
        Log.d(TAG, "onNewIntent: installNewVer=$installNewVer")
        proceedIntent()
    }

    private fun onUpdateDownloadCallback(checkResult: UpdateCheckResult, downloadState: UpdateDownloadState, byteDownload:Long, byteNeedDown:Long){
        UpdateUtils.showDownloadedNotification(this,downloadState,byteDownload,byteNeedDown){
            if(it == UpdateDownloadState.SUCCEED){
                UpdateUtils.install(this)
            }
        }
    }

    override fun getBaseViewModel() = null

    private fun proceedIntent(){
        intent?.let {
            lifecycleScope.launch {
                val activity = this@MainActivity
                flow { emit(intent) }
                    .flowOn(Dispatchers.Main)
                    .transform {
                        // from notification
                        //检查更新
                        val installNewVer = intent.getBooleanExtra("installNewVer",false)
                        Log.d(TAG, "onCreate: checkUpdate=$installNewVer")
                        if(installNewVer){
                            UpdateUtils.install(activity)
                        }else{
                            emit(it)
                        }
                    }
                    .transform{
                        // launch permission check
                        val permissions = ArrayList<PermissionItem>()
                        if(CallScreenRoleManager.isRoleAvailable()){
                            if(!CallScreenRoleManager.isRoleHeld()){
                                permissions.add(
                                    PermissionItem(
                                        LaunchPermissionDialog.ROLE_PERMISSION,
                                        getString(R.string.default_caller_id),
                                        R.mipmap.ic_phone_blue,
                                        getString(R.string.default_caller_id_subtitle)
                                    )
                                )
                            }
                        }else{
                            PRDialog.Builder()
                                .setTitle(activity.getString(R.string.important))
                                .setContent(activity.getString(R.string.device_not_support))
                                .setRightBtnName(activity.getString(com.zaz.support.R.string.yes))
                                .show(activity.supportFragmentManager)
                            return@transform
                        }

                        if(!isPermissionGranted(android.Manifest.permission.READ_PHONE_STATE)){
                            //取消通知
                            activity.sendBroadcast(
                                Intent(StaticsBroadcast.ACTION_CANCEL_NOTIFICATION).apply {
                                    putExtra(
                                        StaticsBroadcast.Notification_ID,
                                        Constant.NotificationId.NO_READ_PHONE_STATE_PERMISSION
                                    )
                                setPackage(packageName)
                            })
                            permissions.add(
                                PermissionItem(
                                    LaunchPermissionDialog.READ_PHONE_STATE,
                                    getString(R.string.read_phone_state),
                                    R.mipmap.ic_phone_blue,
                                    getString(R.string.read_phone_state_subtitle)
                                )
                            )
                        }
                        if(permissions.isNotEmpty()){
                            LaunchPermissionDialog.show(activity, permissions)
                        }else{
                            emit(it)
                        }
                    }
//                    .transform {
//                        //检查更新
//                        UpdateUtils.checkUpdate(activity){
//                            Log.d(TAG, "checkUpdate: result=$it")
//                            if(it != UpdateCheckResult.NO_UPDATE){
//                                UpdateUtils.startUpdate(it, StartUpdateConfig().apply {
//                                    this.context = this@MainActivity
//                                    callback = ::onUpdateDownloadCallback
//                                    googleUpdateLauncher = gpUpdate
//                                })
//                            }else{
//                                launch {
//                                    emit(it)
//                                }
//                            }
//                        }
//                    }
                    .collect{
                        Log.d(TAG, "proceedIntent: intent proceed complete!")
                    }
            }
        }
    }
}
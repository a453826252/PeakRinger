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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zaz.peakringer.CallScreenRoleManager
import com.zaz.peakringer.R
import com.zaz.peakringer.bean.ImgTxtBean
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.databinding.TabMainActivityBottomBinding
import com.zaz.peakringer.dialog.LaunchPermissionDialog
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.display.ContactsFragment
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment
import com.zaz.peakringer.fragment.setting.SettingFragment
import com.zaz.peakringer.utils.UpdateUtils
import com.zaz.support.base.BaseActivity
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
        CallScreenRoleManager.register(this) {
            if (it.resultCode != RESULT_OK) {
                PRToast.show(
                    this,
                    getString(R.string.request_call_screen_permission)
                )
            }
        }
        initView()
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

    private fun onUpdateDownloadCallback(checkResult: UpdateCheckResult, downloadState: UpdateDownloadState, byteDownload:Long, byteNeedDown:Long){
        UpdateUtils.showDownloadedNotification(this,downloadState,byteDownload,byteNeedDown){
            if(it == UpdateDownloadState.SUCCEED){
                UpdateUtils.install(this)
            }else{
                checkUpdate()
            }
        }
    }

    override fun getBaseViewModel() = null
}
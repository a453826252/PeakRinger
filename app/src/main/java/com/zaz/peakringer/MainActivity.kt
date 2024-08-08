package com.zaz.peakringer

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.zaz.google.AppUpdate
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment
import com.zaz.peakringer.fragment.feedback.FeedbackFragment
import com.zaz.peakringer.utils.UpdateUtils
import com.zaz.support.base.BaseActivity
import com.zaz.support.config.NotificationConfig

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        AppUpdate.checkUpdate(this,::onAppUpdateAvailability)
        UpdateUtils.checkUpdate(this)
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

//    private fun showUpdateNotification(){
//        val notificationConfig = NotificationConfig()
//    }
}
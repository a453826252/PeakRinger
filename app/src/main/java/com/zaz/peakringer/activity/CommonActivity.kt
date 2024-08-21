package com.zaz.peakringer.activity

import android.os.Bundle
import com.zaz.peakringer.R
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment
import com.zaz.peakringer.fragment.feedback.FeedbackFragment
import com.zaz.support.base.BaseActivity

class CommonActivity: BaseActivity() {
    companion object{
        const val FRAGMENT_TYPE_KEY = "fragment_type"
        const val FRAGMENT_TYPE_FEEDBACK = 1
        const val FRAGMENT_TYPE_EDIT_OR_DEL_CONTACTS = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        val type = intent.getIntExtra(FRAGMENT_TYPE_KEY,0)
        val containerId = R.id.common_activity_fragment_container
        val extra = intent.getBundleExtra("extra")
        when(type){
            FRAGMENT_TYPE_FEEDBACK->{
                FeedbackFragment.show(supportFragmentManager,containerId)
            }
            FRAGMENT_TYPE_EDIT_OR_DEL_CONTACTS->{
                EditOrAddContactFragment.show(supportFragmentManager,containerId,extra?.getParcelable("contact"))
            }
            else->{
                finish()
            }
        }
    }
    override fun getBaseViewModel() = null
}
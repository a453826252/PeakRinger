package com.zaz.peakringer

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.zaz.peakringer.databinding.ActivityMainBinding
import com.zaz.peakringer.fragment.contacts.ContactsBean
import com.zaz.peakringer.fragment.contacts.edit.EditOrAddContactFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun showEditOrAddFragment(contact:ContactsBean?){
        EditOrAddContactFragment.show(supportFragmentManager,binding.root.id,contact)
    }
}
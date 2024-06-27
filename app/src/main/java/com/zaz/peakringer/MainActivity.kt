package com.zaz.peakringer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zaz.peakringer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val roleHeld  = CallScreenRoleManager.checkAndRequestRole(this)
        if(roleHeld){

        }
    }
}
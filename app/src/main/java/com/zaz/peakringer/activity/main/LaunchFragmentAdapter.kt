package com.zaz.peakringer.activity.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LaunchFragmentAdapter(activity: MainActivity, private val fragments:List<Fragment>): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int  =fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
package com.oneconnect.demoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oneconnect.demoapp.fragments.FreeServersFragment
import com.oneconnect.demoapp.fragments.ProServersFragment

class ServerPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FreeServersFragment()
            1 -> ProServersFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}

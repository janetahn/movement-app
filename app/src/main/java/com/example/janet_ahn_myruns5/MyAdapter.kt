package com.example.janet_ahn_myruns5

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class MyAdapter (activity: FragmentActivity, var list: ArrayList<Fragment>) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
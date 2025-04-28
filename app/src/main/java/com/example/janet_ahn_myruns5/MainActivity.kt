package com.example.janet_ahn_myruns5

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private lateinit var startFragment: StartFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tabLayout: TabLayout
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        startFragment = StartFragment()
        historyFragment = HistoryFragment()
        settingsFragment = SettingsFragment()

        fragments = ArrayList()
        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingsFragment)

        val list = arrayOf(getString(R.string.start_title), getString(R.string.history_title), getString(R.string.settings_title));

        val adapter = MyAdapter(this, fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position: Int -> tab.text = list[position] }.attach()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        tabLayoutMediator.detach()
//    }

    fun onClick(view: View){
        println("debug: button clicked")
        val intent: Intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun onActivitySaveClick(view: View) {
        startFragment.onActivitySaveClick(view)
    }

}
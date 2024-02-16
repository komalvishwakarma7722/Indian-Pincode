package com.example.indianpincodes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.indianpincodes.databinding.ActivityMainBinding
import com.example.indianpincodes.fragment.QuickSearchFragment
import com.example.indianpincodes.fragment.SaveRecordFragment
import com.example.indianpincodes.fragment.SearchAreaFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewpager2 :ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var appPagerAdapter: AppPagerAdapter

    private val title = arrayListOf("QUICK SEARCH","SEARCH BY AREA","SAVE RECORD")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout=binding.tablayout
        viewpager2=binding.viewpager2

        appPagerAdapter=AppPagerAdapter(this)
        viewpager2.adapter=appPagerAdapter
        TabLayoutMediator(tabLayout,viewpager2){
            tab,position->
            tab.text=title[position]
        }.attach()

        // Access the SearchView through view binding
//        val searchView = binding.search
        // Now you can use the searchView as needed



    }
    class AppPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> QuickSearchFragment()
                1->SearchAreaFragment()
                2->SaveRecordFragment()
                else -> QuickSearchFragment()
            }
        }
    }
}
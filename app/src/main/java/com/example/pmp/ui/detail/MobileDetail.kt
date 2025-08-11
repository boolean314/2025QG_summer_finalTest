package com.example.pmp.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pmp.R
import com.example.pmp.databinding.ActivityMobileDetailBinding
import com.example.pmp.ui.detail.fragment.BackendErrorFragment
import com.example.pmp.ui.detail.fragment.BackendPerformanceFragment
import com.example.pmp.ui.detail.fragment.MobileErrorFragment
import com.example.pmp.ui.detail.fragment.MobilePerformanceFragment
import com.google.android.material.tabs.TabLayoutMediator

class MobileDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding= DataBindingUtil.setContentView<ActivityMobileDetailBinding>(this,R.layout.activity_mobile_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tabLayout=binding.mobileDetailTab
        val viewPager=binding.mobileDetailViewpager
        viewPager.adapter=MobileViewPagerAdapter(this)
        TabLayoutMediator(tabLayout,viewPager){
            tab,position->tab.text=when(position){

            0->"异常"
            1->"性能"
            else->""
        }
        }.attach()
    }




    class MobileViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MobileErrorFragment()
                1 ->  MobilePerformanceFragment()
                else ->throw IllegalArgumentException("Invalid position")
            }
        }
    }
}
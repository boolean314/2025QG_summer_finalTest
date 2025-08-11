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
import com.example.pmp.databinding.ActivityBackendDetailBinding
import com.example.pmp.ui.detail.fragment.BackendErrorFragment
import com.example.pmp.ui.detail.fragment.BackendPerformanceFragment
import com.example.pmp.ui.detail.fragment.FrontendBehaviorFragment
import com.example.pmp.ui.detail.fragment.FrontendErrorFragment
import com.example.pmp.ui.detail.fragment.FrontendPerformanceFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BackendDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       val binding= DataBindingUtil.setContentView<ActivityBackendDetailBinding>(this,R.layout.activity_backend_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tabLayout=binding.backendDetailTab
        val viewPager=binding.backendDetailViewpager
        viewPager.adapter=BackendViewPagerAdapter(this)
        TabLayoutMediator(tabLayout,viewPager){
            tab,position->tab.text=when(position){
                0->"异常"
                1->"性能"
                else->""
            }
        }.attach()
    }




    class BackendViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BackendErrorFragment()
                1 ->  BackendPerformanceFragment()
                else ->throw IllegalArgumentException("Invalid position")
            }
        }
    }
}



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
import com.example.pmp.databinding.ActivityFrontendDetailBinding
import com.example.pmp.ui.detail.fragment.FrontendBehaviorFragment
import com.example.pmp.ui.detail.fragment.FrontendErrorFragment
import com.example.pmp.ui.detail.fragment.FrontendPerformanceFragment
import com.google.android.material.tabs.TabLayoutMediator

class FrontendDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding=DataBindingUtil.setContentView<ActivityFrontendDetailBinding>(this,R.layout.activity_frontend_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
       val tabLayout= binding.frontendDetailTab
        val viewPager=binding.frontendDetailViewpager
        viewPager.adapter = FrontendViewPagerAdapter(this)
        TabLayoutMediator(tabLayout,viewPager){
                tab,position->tab.text=when(position){
            0->"行为"
            1->"异常"
            2->"性能"
            else->""
        }
        }.attach()

    }
    class FrontendViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FrontendBehaviorFragment()
                1 ->  FrontendErrorFragment()
                2 ->FrontendPerformanceFragment()
                else ->throw IllegalArgumentException("Invalid position")
            }
        }
    }
}



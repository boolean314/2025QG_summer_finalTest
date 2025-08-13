package com.example.pmp.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pmp.R
import com.example.pmp.databinding.ActivityFrontendDetailBinding
import com.example.pmp.ui.detail.fragment.FrontendBehaviorFragment
import com.example.pmp.ui.detail.fragment.FrontendErrorFragment
import com.example.pmp.ui.detail.fragment.FrontendPerformanceFragment
import com.example.pmp.ui.detail.fragment.MobileErrorFragment
import com.example.pmp.ui.detail.fragment.MobilePerformanceFragment
import com.example.pmp.viewModel.FrontendDetailVM
import com.example.pmp.viewModel.MobileDetailVM
import com.google.android.material.tabs.TabLayoutMediator

class FrontendDetail : AppCompatActivity() {
    private val PROJECTID:String="111"
    private val PROJECTNAME:String="111"
    private val PROJECTPERMISSION:String="111"
    private var projectId:String?=null
    private var projectName:String?=null
    private var projectPermission:String?=null
    private lateinit var viewModel: FrontendDetailVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = DataBindingUtil.setContentView<ActivityFrontendDetailBinding>(
            this,
            R.layout.activity_frontend_detail
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[FrontendDetailVM::class.java]
        val tabLayout = binding.frontendDetailTab
        val viewPager = binding.frontendDetailViewpager

        //获取外面传进来的projectId等信息并且传递给fragment
        //receiveProjectData(PROJECTID,PROJECTNAME,PROJECTPERMISSION)

        viewPager.adapter = FrontendViewPagerAdapter(this, projectId)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "行为"
                1 -> "错误"
                2 -> "性能"
                else -> ""
            }
        }.attach()


        // 将ViewModel设置到binding中
        binding.viewModel = viewModel
        binding.lifecycleOwner = this // 确保LiveData能够正确更新UI


    }
        //接收project数据
        fun receiveProjectData(key1:String,key2:String,key3:String){
            projectId=intent.getStringExtra(key1)
            projectName=intent.getStringExtra(key2)
            projectPermission=intent.getStringExtra(key3)
            Log.d("MobileDetail","projectId:$projectId,projectName:$projectName，projectPermission:$projectPermission")
            viewModel.setProjectData(projectId!!,projectName!!,projectPermission!!)
        }






        // 修改 Adapter，添加构造参数来接收项目数据
        class FrontendViewPagerAdapter(
            fragmentActivity: FragmentActivity,
            private val projectId: String?,
        ) : FragmentStateAdapter(fragmentActivity) {

            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                val fragment = when (position) {
                    0 -> FrontendBehaviorFragment()
                    1 ->  FrontendErrorFragment()
                    2 ->FrontendPerformanceFragment()

                    else -> throw IllegalArgumentException("Invalid position")
                }

                // 创建 Bundle 并传递数据给 Fragment
                val bundle = Bundle().apply {
                    putString("projectId", projectId)
                }
                fragment.arguments = bundle

                return fragment
            }
        }
}



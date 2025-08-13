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
import com.example.pmp.databinding.ActivityBackendDetailBinding
import com.example.pmp.ui.detail.fragment.BackendErrorFragment
import com.example.pmp.ui.detail.fragment.BackendPerformanceFragment
import com.example.pmp.ui.detail.fragment.FrontendBehaviorFragment
import com.example.pmp.ui.detail.fragment.FrontendErrorFragment
import com.example.pmp.ui.detail.fragment.FrontendPerformanceFragment
import com.example.pmp.viewModel.BackendDetailVM
import com.example.pmp.viewModel.FrontendDetailVM
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BackendDetail : AppCompatActivity() {
    private val PROJECTID:String="111"
    private val PROJECTNAME:String="111"
    private val PROJECTPERMISSION:String="111"
    private var projectId:String?=null
    private var projectName:String?=null
    private var projectPermission:String?=null
    private lateinit var viewModel: BackendDetailVM
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
        viewModel=ViewModelProvider(this)[BackendDetailVM::class.java]
        viewPager.adapter=BackendViewPagerAdapter(this, projectId)
        TabLayoutMediator(tabLayout,viewPager){
            tab,position->tab.text=when(position){
                0->"错误"
                1->"性能"
                else->""
            }
        }.attach()
        // 将ViewModel设置到binding中
        binding.viewModel = viewModel
        binding.lifecycleOwner = this // 确保LiveData能够正确更新UI
        viewModel.setProjectData("111","美团后台","公开")
        //receiveProjectData(PROJECTID,PROJECTNAME,PROJECTPERMISSION)
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
    class BackendViewPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val projectId: String?,
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> BackendErrorFragment()
                1 ->  BackendPerformanceFragment()
                else ->throw IllegalArgumentException("Invalid position")
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



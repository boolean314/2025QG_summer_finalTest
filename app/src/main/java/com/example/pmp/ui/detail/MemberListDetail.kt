package com.example.pmp.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.databinding.ActivityMemberListDetailBinding
import com.example.pmp.ui.adapter.MemberListAdapter
import com.example.pmp.viewModel.MemberListDetailVM

class MemberListDetail : AppCompatActivity() {
    private lateinit var binding: ActivityMemberListDetailBinding
    private lateinit var apiService: MyApiService
    private lateinit var projectId: String
    private lateinit var memberListViewModel: MemberListDetailVM
    private lateinit var memberListAdapter: MemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_list_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = ServiceCreator.create(MyApiService::class.java)
        projectId = intent.getStringExtra("projectId") ?: ""
        Log.d("MemberListDetail", "projectId: $projectId")

        memberListViewModel = ViewModelProvider(this).get(MemberListDetailVM::class.java)
        memberListViewModel.setData(projectId)

        setupRecyclerView()
        observeViewModel()
        memberListViewModel.getMemberList()

        // 注册广播接收器监听刷新事件
        registerRefreshReceiver()
    }

    private fun registerRefreshReceiver() {
        val filter = android.content.IntentFilter("REFRESH_MEMBER_LIST")
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
            .registerReceiver(refreshReceiver, filter)
    }

    private val refreshReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
            // 收到刷新请求，重新获取成员列表
            memberListViewModel.getMemberList()
        }
    }

    private fun setupRecyclerView() {
        memberListAdapter = MemberListAdapter(emptyList())
        // 设置项目ID，以便在Adapter中使用
        memberListAdapter.setProjectId(projectId)
        binding.memberListRecyclerview.apply {
            adapter = memberListAdapter
            layoutManager = LinearLayoutManager(this@MemberListDetail)
        }
    }

    private fun observeViewModel() {
        memberListViewModel.memberList.observe(this) { memberList ->
            memberListAdapter = MemberListAdapter(memberList)
            memberListAdapter.setProjectId(projectId)
            binding.memberListRecyclerview.adapter = memberListAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(refreshReceiver)
        } catch (e: Exception) {
            Log.e("MemberListDetail", "注销广播接收器失败", e)
        }
    }
}

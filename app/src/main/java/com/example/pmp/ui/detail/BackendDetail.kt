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
import com.example.pmp.data.model.ProjectDetail
import com.example.pmp.databinding.ActivityBackendDetailBinding
import com.example.pmp.databinding.DialogEditWebhookBinding
import com.example.pmp.ui.detail.fragment.BackendErrorFragment
import com.example.pmp.ui.detail.fragment.BackendLogFragment
import com.example.pmp.ui.detail.fragment.BackendPerformanceFragment
import com.example.pmp.viewModel.BackendDetailVM
import com.example.pmp.viewModel.DetailVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class BackendDetail : AppCompatActivity() {
    private val PROJECTID: String = "extra_uuid"
    private val USERID: String = "extra_userId"
    private val USERROLE: String = "extra_userRole"
    private var projectId: String? = null
    private var userId: Long = 0
    private var userRole: Int = 0
    private lateinit var viewModel: DetailVM
    private lateinit var webhook: String
    private lateinit var bigBinding: ActivityBackendDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bigBinding = DataBindingUtil.setContentView<ActivityBackendDetailBinding>(
            this,
            R.layout.activity_backend_detail
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[DetailVM::class.java]
        val tabLayout = bigBinding.backendDetailTab
        val viewPager = bigBinding.backendDetailViewpager
        webhook = bigBinding.backendDetailWebhookCode.text.toString()

        // 将ViewModel设置到binding中
        bigBinding.viewModel = viewModel
        bigBinding.lifecycleOwner = this // 确保LiveData能够正确更新UI
        receiveProjectData(PROJECTID, USERID, USERROLE)

        viewPager.adapter = BackendViewPagerAdapter(this, projectId)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "错误"
                1 -> "性能"
                2 -> "日志"
                else -> ""
            }
        }.attach()

        // 添加编辑webhook图标点击监听
        bigBinding.backendDetailEditWebhook.setOnClickListener {
            showEditWebhookDialog()
        }
    }

    //弹出对话框
    private fun showEditWebhookDialog() {
        // 使用 DataBinding 创建视图
        val binding: DialogEditWebhookBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_edit_webhook,
            null,
            false
        )

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(binding.root)  // 使用 binding.root 作为对话框视图
            .create()
        dialog.show()
        binding.editWebhookButton.setOnClickListener {
            val newWebhook = binding.editWebhook.text.toString()
            updateWebhookInViewModel(newWebhook)
            dialog.dismiss()
        }
    }

    //更新webhook并且发送网络请求
    private fun updateWebhookInViewModel(newWebhook: String) {
        viewModel.webhook.value = newWebhook
        Log.d("BackendDetail", "newWebhook:$newWebhook")
        val projectDetail = ProjectDetail(
            uuid = projectId!!,
            name = null,
            description = null,
            createdTime = null,
            isPublic = null,
            webhook = newWebhook,
            inviteCode = null,
            groupCode = null,
            isDeleted = null
        )
        viewModel.updateProject(projectDetail, this)

    }


    //接收project数据
    fun receiveProjectData(key1: String, key2: String, key3: String) {
        projectId = intent.getStringExtra(key1)
        userId = intent.getLongExtra(key2, 0)
        userRole = intent.getIntExtra(key3, 0)
        Log.d("BackendDetail", "projectId:$projectId,userId:$userId,userRole:$userRole")
        viewModel.setData(projectId!!, userId, userRole)
    }



    // 修改 Adapter，添加构造参数来接收项目数据
    class BackendViewPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val projectId: String?,
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> BackendErrorFragment()
                1 -> BackendPerformanceFragment()
                2 -> BackendLogFragment()
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

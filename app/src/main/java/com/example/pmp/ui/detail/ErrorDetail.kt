package com.example.pmp.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.JoinProjectData
import com.example.pmp.databinding.ActivityErrorDetailBinding
import com.example.pmp.databinding.DialogAssignMemberBinding
import com.example.pmp.databinding.DialogJoinProjectBinding
import com.example.pmp.databinding.DialogUpdateThresholdBinding
import com.example.pmp.ui.adapter.AssignMemberAdapter
import com.example.pmp.viewModel.ErrorDetailVM
import com.example.pmp.viewModel.MemberListDetailVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Response

class ErrorDetail : AppCompatActivity() {
    private var errorId=0
    private lateinit var platform:String
    private lateinit var errorViewModel: ErrorDetailVM
    private lateinit var binding: ActivityErrorDetailBinding
    private lateinit var setThreshold: TextView
    private lateinit var assignMemberText:TextView
    private lateinit var memberListViewModel: MemberListDetailVM

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= DataBindingUtil.setContentView<ActivityErrorDetailBinding>(this,R.layout.activity_error_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        platform=intent.getStringExtra("platform")!!
        errorId=intent.getIntExtra("errorId",0)
        Log.d("ErrorDetail", "errorId: $errorId,platform: $platform")
        errorViewModel= ViewModelProvider(this).get(ErrorDetailVM::class.java)
        binding.errorDetailVM=errorViewModel
        binding.lifecycleOwner=this
        setThreshold=binding.errorThreshold
        assignMemberText=binding.errorAssign

        // 初始化 MemberListDetailVM
        memberListViewModel = ViewModelProvider(this).get(MemberListDetailVM::class.java)

        when(platform){
            "frontend"->errorViewModel.getFrontendErrorDetail(errorId,platform)
            "backend"->errorViewModel.getBackendErrorDetail(errorId,platform)
            "mobile"->errorViewModel.getMobileErrorDetail(errorId,platform)
        }
        // 同时观察 errorType 和 projectId
        errorViewModel.errorType.observe(this) { errorType ->
            if (!errorType.isNullOrEmpty() && !errorViewModel.projectId.value.isNullOrEmpty()) {
                errorViewModel.getThreshold(platform)
                errorViewModel.getHandleStatus(platform)
                // 设置项目ID到memberListViewModel
                memberListViewModel.setData(errorViewModel.projectId.value!!)
            }
        }

        errorViewModel.projectId.observe(this) { projectId ->
            if (!projectId.isNullOrEmpty() && !errorViewModel.errorType.value.isNullOrEmpty()) {
                errorViewModel.getThreshold(platform)
                errorViewModel.getHandleStatus(platform)
                // 设置项目ID到memberListViewModel
                memberListViewModel.setData(projectId)
            }
        }
        setThreshold.setOnClickListener {
            showUpdateThresholdDialog()
        }
        assignMemberText.setOnClickListener {
            showAssignMemberDialog()
        }
    }

    private fun showUpdateThresholdDialog() {
        // 使用 DataBinding 创建视图
        val binding: DialogUpdateThresholdBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_update_threshold,
            null,
            false
        )
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(binding.root)  // 使用 binding.root 作为对话框视图
            .create()
        binding.oldThreshold.text="当前阈值为:${errorViewModel.oldThreshold.value}"
        // 在这里设置点击监听器
        binding.updateThresholdButton.setOnClickListener {
            if(binding.updateThresholdEditText.text.toString().isEmpty()){
                Toast.makeText(this, "请输入阈值", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val threshold=binding.updateThresholdEditText.text.toString().toInt()
            errorViewModel.updateThreshold(platform,threshold)
            dialog.dismiss()
            Toast.makeText(this, "阈值更新成功", Toast.LENGTH_SHORT).show()

        }
        dialog.show()
    }

    private fun showAssignMemberDialog() {
        // 使用 DataBinding 创建视图
        val dialogBinding: DialogAssignMemberBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_assign_member,
            null,
            false
        )

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        // 创建并设置适配器
        val assignMemberAdapter = AssignMemberAdapter(emptyList()) { member ->
            // 在这里实现指派成员的逻辑
            assignErrorToMember(member)
            dialog.dismiss()
        }

        // 设置RecyclerView
        dialogBinding.assignMemberRecyclerview.apply {
            adapter = assignMemberAdapter
            layoutManager = LinearLayoutManager(this@ErrorDetail)
        }

        // 获取成员列表
        memberListViewModel.getMemberList()

        // 观察成员列表变化
        memberListViewModel.memberList.observe(this) { memberList ->
            // 只传递 userRole 为 2 的成员给适配器
            val filteredList = memberList.filter { it.userRole == 2 }
            assignMemberAdapter.updateMemberList(filteredList)
        }

        dialog.show()
    }

    private fun assignErrorToMember(member: com.example.pmp.data.model.MemberListData) {
        // 实现指派错误给成员的逻辑
        errorViewModel.assignMember(member.userId)
        Toast.makeText(this, "已将错误指派给: ${member.username}", Toast.LENGTH_LONG).show()

    }
}

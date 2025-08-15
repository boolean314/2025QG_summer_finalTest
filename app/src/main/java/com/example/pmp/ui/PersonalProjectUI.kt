package com.example.pmp.ui

import android.annotation.SuppressLint


import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.JoinProjectData
import com.example.pmp.databinding.DialogJoinProjectBinding
import com.example.pmp.ui.adapter.PersonalProjectAdapter
import com.example.pmp.viewModel.JoinProjectVM
import com.example.pmp.viewModel.PersonalProjectVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABShape
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil

import androidx.lifecycle.lifecycleScope
import com.example.pmp.ui.adapter.PublicProjectAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
class PersonalProjectUI : Fragment(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Int> {

    private lateinit var rfaLayout: RapidFloatingActionLayout
    private lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper
    private val viewModel: PersonalProjectVM by viewModels()
    private lateinit var binding:DialogJoinProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_event, container, false)
    }

    override fun onResume() {
        super.onResume()
        val userId = GlobalData.userInfo?.id ?: return
        viewModel.loadProjects(userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rfaLayout = view.findViewById(R.id.rfaLayout)
        rfaButton = view.findViewById(R.id.rfaBtn)
        initRFAB()
        val recyclerView = view.findViewById<RecyclerView>(R.id.all_recycler_view)
        val swipeRefresh = view.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeRefresh)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding=DialogJoinProjectBinding.inflate(layoutInflater)
        binding.joinProjectButton.setOnClickListener{
            val invitedCode=binding.joinProjectId.text.toString()
            val userId=GlobalData.userInfo?.id?.toLong()
            Log.d("PersonalProjectUI", "invitedCode: $invitedCode")
            Log.d("PersonalProjectUI", "userId: $userId")

        }

        val userId = GlobalData.userInfo?.id ?: return
        viewModel.loadProjects(userId)
        swipeRefresh.setColorSchemeResources(R.color.qq_blue)
        swipeRefresh.setOnRefreshListener {
            refreshProject(recyclerView.adapter as PersonalProjectAdapter, swipeRefresh)
        }
        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            val adapter = PersonalProjectAdapter(
                projects.toMutableList(),
                { uuid ->
                    viewModel.deleteProject(uuid) { success ->
                        if (success) {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                { projectId, userId ->
                    viewModel.exitProject(projectId, userId) { success ->
                        if (success) {
                            Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "退出失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                viewModel
            ){ userId, projectId, callback ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val canDelete = viewModel.authenticate(userId, projectId)
                    callback(canDelete)
                }
            }
            recyclerView.adapter = adapter
        }

        val searchEditText = view.findViewById<TextInputLayout>(R.id.search_bar).editText
        val searchButton = view.findViewById<android.widget.ImageButton>(R.id.search_button)

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    viewModel.filterProjectsByName("")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchButton.setOnClickListener {
            val query = searchEditText?.text?.toString() ?: ""
            viewModel.filterProjectsByName(query)
        }
    }

    private fun initRFAB() {
        val context = requireContext()
        val content = RapidFloatingActionContentLabelList(context)
        content.setOnRapidFloatingActionContentLabelListListener(this)

        val items = listOf(
            RFACLabelItem<Int>()
                .setLabel("创建项目")
                .setResId(R.drawable.ic_action_add_circle_outline)
                .setLabelSizeSp(16)
                .setIconNormalColor(0xff00cc99.toInt())
                .setIconPressedColor(0xff00cc66.toInt())
                .setLabelBackgroundDrawable(
                    RFABShape.generateCornerShapeDrawable(
                        0xffffffff.toInt(),
                        RFABTextUtil.dip2px(context, 4f)
                    )
                ),
            RFACLabelItem<Int>()
                .setLabel("加入项目")
                .setResId(R.drawable.ic_action_arrow_forward_light)
                .setIconNormalColor(0xff00cccc.toInt())
                .setIconPressedColor(0xff0099cc.toInt())
                .setLabelSizeSp(16)
                .setLabelBackgroundDrawable(
                    RFABShape.generateCornerShapeDrawable(
                        0xffffffff.toInt(),
                        RFABTextUtil.dip2px(context, 4f)
                    )
                )
        )
        content.setItems(items)
            .setIconShadowRadius(RFABTextUtil.dip2px(context, 5f))
            .setIconShadowColor(0xff888888.toInt())
            .setIconShadowDy(RFABTextUtil.dip2px(context, 1f))

        rfabHelper = RapidFloatingActionHelper(
            context,
            rfaLayout,
            rfaButton,
            content
        ).build()
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<Int>) {
        when(position) {
            0 -> {
                startActivity(Intent(context, CreateProject::class.java))
            }
            1 ->  showJoinProjectDialog()
        }
        rfabHelper.collapseContent()
    }

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Int>) {
        when(position) {
            0 -> {
                startActivity(Intent(context, CreateProject::class.java))
            }
            1 ->  showJoinProjectDialog()
        }
        rfabHelper.collapseContent()
    }
    private fun showJoinProjectDialog() {
        // 使用 DataBinding 创建视图
        val binding: DialogJoinProjectBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_join_project,
            null,
            false
        )

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)  // 使用 binding.root 作为对话框视图
            .create()




        //加入项目未测试
        // 在这里设置点击监听器
        binding.joinProjectButton.setOnClickListener {
            val invitedCode = binding.joinProjectId.text.toString()
            val userId =  GlobalData.userInfo?.id?.toLong()
            Log.d("PersonalProjectUI", "invitedCode: $invitedCode")
            Log.d("PersonalProjectUI", "userId: $userId")

            // 在这里添加加入项目的逻辑
            if (invitedCode.isNotEmpty() && userId != null) {
                val apiService=ServiceCreator.create(MyApiService::class.java)
                val joinProjectData= JoinProjectData(userId,invitedCode)
                apiService.joinProject(joinProjectData).enqueue(object:retrofit2.Callback<ApiResponse<Any>>{
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        Log.d("PersonalProjectUI", "response: $response")
                        Log.d("PersonalProjectUI", "responseBody: ${response.body()}")
                        if(response.isSuccessful){
                            Toast.makeText(context, "加入项目成功", Toast.LENGTH_SHORT).show()
                            dialog.dismiss() // 根据需要关闭对话框
                        }
                        else{
                            Toast.makeText(context, "加入项目失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        Log.d("PersonalProjectUI", "onFailure: $t")
                        Toast.makeText(context, "请输入正确的邀请码", Toast.LENGTH_SHORT).show()
                    }

                })
            }else{
                Toast.makeText(context, "请输入项目邀请码", Toast.LENGTH_SHORT).show()
            }


        }

        dialog.show()

}


    @SuppressLint("NotifyDataSetChanged")
    fun refreshProject(adapter: PersonalProjectAdapter, swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            val userId = GlobalData.userInfo?.id ?: return@launch
            viewModel.loadProjects(userId)
            requireActivity().runOnUiThread{
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
        }
    }
}
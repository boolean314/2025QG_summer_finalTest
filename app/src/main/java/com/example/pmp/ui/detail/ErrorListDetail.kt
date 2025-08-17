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
import com.example.pmp.databinding.ActivityErrorListDetailBinding
import com.example.pmp.ui.adapter.ErrorListAdapter
import com.example.pmp.viewModel.ErrorListDetailVM

class ErrorListDetail : AppCompatActivity() {
    private lateinit var projectId: String
    private lateinit var platform: String
    private lateinit var viewModel: ErrorListDetailVM
    private lateinit var binding: ActivityErrorListDetailBinding
    private lateinit var errorAdapter: ErrorListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 使用DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_error_list_detail)
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("projectId").toString()
        platform = intent.getStringExtra("platform").toString()
        Log.d("ErrorListDetail", "projectId: ${projectId} , platform: ${platform}")

        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[ErrorListDetailVM::class.java]
        binding.viewModel = viewModel

        // 初始化RecyclerView和Adapter
        setupRecyclerView()

        // 设置数据
        viewModel.setData(projectId, platform)
    }

    private fun setupRecyclerView() {
        errorAdapter = ErrorListAdapter()
        errorAdapter.platform = platform  // 将Activity中的platform传递给Adapter
        binding.errorRecyclerView.adapter = errorAdapter

        // 观察错误列表数据变化
        viewModel.errorList.observe(this) { errorList ->
            errorAdapter.submitList(errorList)
        }
    }


}

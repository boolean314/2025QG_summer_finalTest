// ErrorListDetail.kt
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_error_list_detail)
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("projectId").toString()
        platform = intent.getStringExtra("platform").toString()
        Log.d("ErrorListDetail", "projectId: $projectId , platform: $platform")

        viewModel = ViewModelProvider(this)[ErrorListDetailVM::class.java]
        binding.viewModel = viewModel

        setupRecyclerView()

        viewModel.setData(projectId, platform)

        // 根据平台类型观察不同的 LiveData
        when (platform) {
            "frontend" -> {
                viewModel.errorList.observe(this) { errorList ->
                    errorAdapter.submitList(errorList)
                }
            }
            "backend" -> {
                viewModel.errorListBackend.observe(this) { errorListBackend ->
                    errorAdapter.submitList(errorListBackend)
                }
            }
            "mobile" -> {
                viewModel.errorListMobile.observe(this) { errorListMobile ->
                    errorAdapter.submitList(errorListMobile)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        errorAdapter = ErrorListAdapter()
        errorAdapter.platform = platform

        binding.errorRecyclerView.apply {
            adapter = errorAdapter
            layoutManager = LinearLayoutManager(this@ErrorListDetail)
        }
    }
}

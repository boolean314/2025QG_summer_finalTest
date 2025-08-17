// BackendLogFragment.kt
package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.databinding.FragmentBackendLogBinding
import com.example.pmp.viewModel.FragmentErrorVM
import com.github.mikephil.charting.charts.BarChart

class BackendLogFragment : Fragment(R.layout.fragment_backend_log) {
    private var projectId: String? = null
    private lateinit var binding: FragmentBackendLogBinding
    private lateinit var viewModel: FragmentErrorVM
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments?.getString("projectId")
        Log.d("BackendLogFragment", "projectId: $projectId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backend_log, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 BarChart
        barChart = binding.backendLogBarChart

        // 初始化 ViewModel
        viewModel = ViewModelProvider(this).get(FragmentErrorVM::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 检查projectId是否为空
        if (projectId.isNullOrEmpty()) {
            Log.w("BackendLogFragment", "projectId is null or empty, using default value 'pro-52038057'")
            projectId = "pro-52038057"
        }

        // 设置数据到 ViewModel
        viewModel.setData(projectId!!, requireContext(), barChart)
    }
}

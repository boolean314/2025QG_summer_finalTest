package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.databinding.FragmentFrontendPerformanceBinding
import com.example.pmp.viewModel.FragmentPerformanceVM
import com.github.mikephil.charting.charts.BarChart

class FrontendPerformanceFragment:Fragment(R.layout.fragment_frontend_performance) {
    private var projectId: String? = null
    private lateinit var binding: FragmentFrontendPerformanceBinding
    private lateinit var barChart: BarChart
    private lateinit var viewModel: FragmentPerformanceVM
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments?.getString("projectId")
        Log.d("FrontendPerformanceFragment", "Project ID: $projectId")
        // 检查projectId是否为空
        if (projectId.isNullOrEmpty()) {
            Log.w("FrontendPerformanceFragment", "projectId is null or empty, using default value 'pro-52038057'")
            projectId = "pro-52038057"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_frontend_performance,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart=binding.frontendPerformance1BarChart
        progressBar=binding.frontendPerformance1ProgressBar
        viewModel= ViewModelProvider(this).get(FragmentPerformanceVM::class.java)
        binding.clickHandler=viewModel
        binding.lifecycleOwner=this

        viewModel.sendRequest(barChart,projectId!!,"frontend","day",requireContext(),progressBar)
    }


}
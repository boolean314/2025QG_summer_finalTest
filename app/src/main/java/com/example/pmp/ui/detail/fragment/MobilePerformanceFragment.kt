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
import com.example.pmp.databinding.FragmentBackendPerformanceBinding
import com.example.pmp.databinding.FragmentMobilePerformanceBinding
import com.example.pmp.viewModel.FragmentPerformanceVM
import com.github.mikephil.charting.charts.BarChart

class MobilePerformanceFragment: Fragment(R.layout.fragment_mobile_performance) {
    private var projectId: String? = null
    private lateinit var binding: FragmentMobilePerformanceBinding
    private lateinit var barChart: BarChart
    private lateinit var viewModel: FragmentPerformanceVM
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments?.getString("projectId")
        Log.d("MobilePerformanceFragment", "Project ID: $projectId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_mobile_performance,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart=binding.mobilePerformance1BarChart
        progressBar=binding.mobilePerformance1ProgressBar
        viewModel= ViewModelProvider(this).get(FragmentPerformanceVM::class.java)
        binding.clickHandler=viewModel
        binding.lifecycleOwner=this

        viewModel.sendRequest(barChart,projectId!!,"mobile","day",requireContext(),progressBar)
    }
}
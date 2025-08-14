package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.databinding.FragmentFrontendBehaviorBinding
import com.example.pmp.viewModel.FragmentBehaviorVM
import com.github.mikephil.charting.charts.BarChart

class FrontendBehaviorFragment: Fragment(R.layout.fragment_frontend_behavior) {
    private lateinit var binding: FragmentFrontendBehaviorBinding
    private lateinit var viewModel: FragmentBehaviorVM
    private var projectId: String? = null
    private lateinit var barChart: BarChart


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentFrontendBehaviorBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments?.getString("projectId")
        Log.d("FrontendBehaviorFragment", "projectId: $projectId")
        if (projectId == null) {
          projectId="pro-52038057"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(FragmentBehaviorVM::class.java)
        barChart=binding.frontendBehavior1BarChart
        binding.viewModel=viewModel
        binding.lifecycleOwner=this
        viewModel.setData(projectId!!,requireContext(),barChart)

    }

}
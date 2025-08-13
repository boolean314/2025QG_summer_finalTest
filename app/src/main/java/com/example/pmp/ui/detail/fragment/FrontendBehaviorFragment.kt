package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.databinding.FragmentFrontendBehaviorBinding
import com.example.pmp.viewModel.FragmentBehaviorVM

class FrontendBehaviorFragment: Fragment(R.layout.fragment_frontend_behavior) {
    private lateinit var binding: FragmentFrontendBehaviorBinding
    private lateinit var viewModel: FragmentBehaviorVM
    private var projectId: String? = null


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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(FragmentBehaviorVM::class.java)
        binding.viewModel=viewModel
        binding.lifecycleOwner=this
        viewModel.setData("1",requireContext())

    }

}
package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.pmp.R
import com.example.pmp.databinding.FragmentMobileErrorBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MobileErrorFragment:Fragment(R.layout.fragment_mobile_error) {
    private lateinit var binding:FragmentMobileErrorBinding
    private lateinit var lineChart:LineChart
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_mobile_error,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart=binding.lineChart
        setupLineChart()
    }

    private fun setupLineChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }

        // 设置数据
        setData()
    }

    private fun setData() {
        val entries = ArrayList<Entry>()
        // 添加数据点
        entries.add(Entry(0f, 10f))
        entries.add(Entry(1f, 25f))
        entries.add(Entry(2f, 15f))

        val dataSet = LineDataSet(entries, "错误统计")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.orange))

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}
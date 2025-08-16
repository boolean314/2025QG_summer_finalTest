package com.example.pmp.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.AverageTime
import com.example.pmp.data.model.AverageTimeResponse
import com.example.pmp.data.model.GlobalData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Response

class FragmentPerformanceVM : ViewModel() {
    private val apiService = ServiceCreator.create(MyApiService::class.java)
    private lateinit var barChart: BarChart
    private lateinit var projectId: String
    private lateinit var platform: String
    private lateinit var timeType: String
    private lateinit var context:Context
    private lateinit var progressBar: ProgressBar

    fun sendRequest(barChart: BarChart, projectId: String, platform: String, timeType: String,context:Context,progressBar: ProgressBar) {
        this.progressBar=progressBar
        this.barChart = barChart
        this.projectId = projectId
        this.platform = platform
        this.timeType = timeType
        this.context=context
        progressBar.visibility= View.VISIBLE
        Log.d("FragmentPerformanceVM", "Preparing to send request: projectId=$projectId, platform=$platform, timeType=$timeType")
        Log.d("FragmentPerformanceVM", "sendRequest: $projectId $platform $timeType")
        apiService.getAverageTime("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, platform, timeType)
            .enqueue(object : retrofit2.Callback<AverageTimeResponse> {
                override fun onResponse(
                    call: Call<AverageTimeResponse>,
                    response: Response<AverageTimeResponse>
                ) {
                    Log.d("FragmentPerformanceVM", "onResponse: $response")
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.data != null) {
                            // 安全地将 Map<String, Double> 转换为 List<AverageTime> 用于显示
                            val averageTimes = apiResponse.data.map { entry ->
                                AverageTime(entry.key, entry.value.toLong())
                            }
                            Log.d("FragmentPerformanceVM", "Received ${averageTimes.size} data points")
                            displayDataOnChart(averageTimes)
                            progressBar.visibility= View.GONE
                        }else {
                            Log.w("FragmentPerformanceVM", "API response data is null")
                        }
                    } else {
                        Log.e("FragmentPerformanceVM", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<AverageTimeResponse>, t: Throwable) {
                    Log.e("FragmentPerformanceVM", "Network request failed", t)
                }
            })
    }

    private fun displayDataOnChart(averageTimes: List<AverageTime>) {
        Log.d("FragmentPerformanceVM", "Displaying data on chart")

        // 准备数据
        val entries = ArrayList<BarEntry>()
        val apiLabels = ArrayList<String>()

        averageTimes.forEachIndexed { index, averageTime ->
            Log.d("FragmentPerformanceVM", "Processing data point: ${averageTime.api} = ${averageTime.averageTime}")

            // 添加条目数据
            entries.add(BarEntry(index.toFloat(), averageTime.averageTime.toFloat()))

            // 处理API名称，避免过长
            var label = averageTime.api
            if (label.length > 50) {
                label = label.substring(0, 50) + "..."
            }
            apiLabels.add(label)
        }

        if (entries.isEmpty()) {
            Log.w("FragmentPerformanceVM", "No data to display")
            return
        }

        // 创建数据集
        val dataSet = BarDataSet(entries, "平均响应时间 (ms)")

        dataSet.valueTextSize = 10f

        // 创建图表数据
        val barData = BarData(dataSet)
        if(platform.equals("frontend")){
            dataSet.color = ContextCompat.getColor(context, R.color.qq_blue)
        }else if(platform.equals("backend")){
            dataSet.color = ContextCompat.getColor(context, R.color.purple)
        }else{
            dataSet.color = ContextCompat.getColor(context, R.color.orange)
        }
        // 配置图表
        barChart.apply {
            this.data = barData
            description.isEnabled = false
            setFitBars(true)
            invalidate()
        }

        // 配置X轴
        val xAxis = barChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < apiLabels.size) {
                        apiLabels[index]
                    } else {
                        ""
                    }
                }
            }
            granularity = 1f
            labelRotationAngle = -45f
            textSize = 8f
            // 关键修改：处理标签过多的情况
            if (apiLabels.size >15) {  // 调整标签显示密度
                granularity = (apiLabels.size / 15f).toFloat() // 控制标签密度
                setLabelCount(minOf(8, apiLabels.size), false) // 最多显示8个标签
            } else {
                granularity = 1f
            }

            labelRotationAngle = -70f  // 增加倾斜角度到-70度
            textSize = 7f  // 减小字体大小
        }

        // 配置左侧Y轴
        val leftAxis = barChart.axisLeft
        leftAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    return "${value.toInt()}ms"
                }
            }
            textSize = 10f
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f // 确保Y轴从0开始
        }

        // 隐藏右侧Y轴
        barChart.axisRight.isEnabled = false
        if (entries.size > 15) {
            barChart.setVisibleXRangeMaximum(15f) // 初始只显示8个数据点
        }

        // 刷新图表
        barChart.invalidate()

        Log.d("FragmentPerformanceVM", "Chart updated with ${entries.size} data points")
    }
  fun onClickDay(){
        sendRequest(barChart,projectId,platform,"day",context,progressBar)
    }
    fun onClickWeek(){
        sendRequest(barChart,projectId,platform,"week",context,progressBar)
    }
  fun onClickMonth(){
        sendRequest(barChart,projectId,platform,"month",context,progressBar)
    }
}

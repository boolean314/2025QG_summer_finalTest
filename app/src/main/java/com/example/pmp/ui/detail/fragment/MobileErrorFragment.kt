package com.example.pmp.ui.detail.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.ErrorStat
import com.example.pmp.data.model.ErrorTimes
import com.example.pmp.databinding.FragmentMobileErrorBinding
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MobileErrorFragment:Fragment(R.layout.fragment_mobile_error) {
    private var projectId:String?=null
    private lateinit var binding:FragmentMobileErrorBinding
    private lateinit var lineChart:LineChart
    private lateinit var combinedChart:CombinedChart
    private lateinit var entries:MutableList<Entry>
    private lateinit var timeStrings: MutableList<String> // 存储原始时间字符串
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId=arguments?.getString("projectId")
        Log.d("MobileErrorFragment", "projectId: $projectId")
    }

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
        lineChart=binding.mobileError1LineChart
        combinedChart=binding.mobileError2CombinedChart
        entries=mutableListOf<Entry>()
        timeStrings = mutableListOf<String>() // 初始化时间字符串列表

        // 检查projectId是否为空
        if (projectId.isNullOrEmpty()) {
            Log.w("MobileErrorFragment", "projectId is null or empty, using default value '1'")
            projectId = "1"
        }

        // 计算时间参数
        val currentTime = System.currentTimeMillis()
        val startTimeMillis = currentTime - 5*24 * 60 * 60 * 1000 // 5天前
        val startTimeStr = timeFormat.format(Date(startTimeMillis))
        val endTimeStr = timeFormat.format(Date(currentTime))

        Log.d("MobileErrorFragment", "Requesting data for projectId: $projectId, startTime: $startTimeStr, endTime: $endTimeStr")

        // 发送网络请求
        val apiService= ServiceCreator.create(MyApiService::class.java)
        apiService.getErrorTimes(projectId!!, startTimeStr, endTimeStr).enqueue(object : retrofit2.Callback<ApiResponse<List<ErrorTimes>>>{
            override fun onResponse(
                call: retrofit2.Call<ApiResponse<List<ErrorTimes>>>,
                response: retrofit2.Response<ApiResponse<List<ErrorTimes>>>
            ) {
                Log.d("MobileErrorFragment", "Response code: ${response.code()}")
                Log.d("MobileErrorFragment", "Response successful: ${response.isSuccessful}")

                if(response.isSuccessful){
                    val apiResponse = response.body()
                    Log.d("MobileErrorFragment", "API Response: $apiResponse")

                    if (apiResponse != null && apiResponse.data != null) {
                        val errorTimes = apiResponse.data
                        Log.d("MobileErrorFragment", "Error times data: $errorTimes")

                        // 清空之前的数据
                        entries.clear()
                        timeStrings.clear()

                        // 过滤并排序数据
                        val mobileErrorTimes = errorTimes.filter { it.category.equals("mobile", ignoreCase = true) }
                        Log.d("MobileErrorFragment", "Filtered mobile error times count: ${mobileErrorTimes.size}")

                        val sortedErrorTimes = mobileErrorTimes.sortedBy { parseTimeString(it.time) }
                        Log.d("MobileErrorFragment", "Sorted error times count: ${sortedErrorTimes.size}")

                        // 按顺序添加数据点
                        for ((index, errorTime) in sortedErrorTimes.withIndex()) {
                            Log.d("MobileErrorFragment", "Processing errorTime[$index]: $errorTime")
                            // 使用索引作为X轴值
                            entries.add(Entry(index.toFloat(), errorTime.value.toFloat()))
                            // 保存原始时间字符串用于X轴显示
                            timeStrings.add(errorTime.time)
                        }

                        if (sortedErrorTimes.isNotEmpty()) {
                            setupLineChart()
                        } else {
                            Log.w("MobileErrorFragment", "No mobile error data found")
                        }
                    } else {
                        Log.e("MobileErrorFragment", "API response data is null or response is null")
                    }
                } else {
                    Log.e("MobileErrorFragment", "Response not successful. Code: ${response.code()}, Message: ${response.message()}")
                    try {
                        Log.e("MobileErrorFragment", "Error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("MobileErrorFragment", "Failed to read error body", e)
                    }
                }
            }

            // 修复onFailure方法签名，应该匹配Callback<ApiResponse<List<ErrorTimes>>>类型
            override fun onFailure(call: retrofit2.Call<ApiResponse<List<ErrorTimes>>>, t: Throwable) {
                Log.e("MobileErrorFragment", "ErrorTimes_onFailure: ${t.message}", t)
            }
        })


        // 发送网络请求获取错误类型统计数据
        apiService.getMobileErrorStats(projectId!!).enqueue(object : retrofit2.Callback<ApiResponse<List<List<ErrorStat>>>>{
            override fun onResponse(
                call: retrofit2.Call<ApiResponse<List<List<ErrorStat>>>>,
                response: retrofit2.Response<ApiResponse<List<List<ErrorStat>>>>
            ) {
                Log.d("MobileErrorFragment", "Error stats response code: ${response.code()}")
                Log.d("MobileErrorFragment", "Error stats response successful: ${response.isSuccessful}")

                if(response.isSuccessful){
                    val apiResponse = response.body()
                    Log.d("MobileErrorFragment", "Error stats API Response: $apiResponse")

                    // 添加调试信息
                    if (apiResponse != null) {
                        Log.d("MobileErrorFragment", "API Response data class: ${apiResponse.data?.javaClass}")
                        Log.d("MobileErrorFragment", "API Response data: ${apiResponse.data}")

                        if (apiResponse.data != null) {
                            try {
                                val errorStatsData = apiResponse.data
                                Log.d("MobileErrorFragment", "Error stats data size: ${errorStatsData.size}")

                                errorStatsData.forEachIndexed { index, list ->
                                    Log.d("MobileErrorFragment", "List $index size: ${list.size}")
                                    list.forEachIndexed { itemIndex, item ->
                                        Log.d("MobileErrorFragment", "List $index item $itemIndex: $item")
                                    }
                                }

                                if (errorStatsData.isNotEmpty() && errorStatsData.size >= 2) {
                                    // 第一个列表包含count数据
                                    val countData = errorStatsData[0]
                                    // 第二个列表包含ratio数据
                                    val ratioData = errorStatsData[1]

                                    // 处理错误统计数据并设置组合图表
                                    setupCombinedChart(countData, ratioData)
                                } else {
                                    Log.w("MobileErrorFragment", "Error stats data is empty or incomplete")
                                }
                            } catch (e: Exception) {
                                Log.e("MobileErrorFragment", "Error processing error stats data", e)

                                // 尝试使用原始响应体进行调试
                                try {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("MobileErrorFragment", "Raw error body: $errorBody")
                                } catch (e2: Exception) {
                                    Log.e("MobileErrorFragment", "Failed to read raw error body", e2)
                                }
                            }
                        } else {
                            Log.e("MobileErrorFragment", "Error stats API response data is null")
                        }
                    }
                } else {
                    Log.e("MobileErrorFragment", "Error stats response not successful. Code: ${response.code()}, Message: ${response.message()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MobileErrorFragment", "Error stats body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("MobileErrorFragment", "Failed to read error stats body", e)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse<List<List<ErrorStat>>>>, t: Throwable) {
                Log.e("MobileErrorFragment", "ErrorStats_onFailure: ${t.message}", t)
            }
        })

    }

    // 设置组合图表显示错误类型统计数据
    private fun setupCombinedChart(countData: List<ErrorStat>, ratioData: List<ErrorStat>) {
        combinedChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }

        // 准备数据
        val barEntries = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        val lineEntries = ArrayList<Entry>()
        val xLabels = ArrayList<String>()

        // 使用countData作为主要数据源（因为可能ratio数据不完整）
        countData.take(10).forEachIndexed { index, errorStat ->
            // 添加柱状图数据
            val count = errorStat.count ?: 0
            barEntries.add(
                com.github.mikephil.charting.data.BarEntry(
                    index.toFloat(),
                    count.toFloat()
                )
            )

            // 查找对应的ratio数据
            val ratioItem = ratioData.find { it.errorType == errorStat.errorType }
            val ratio = (ratioItem?.ratio ?: 0.0) * 100 // 转换为百分比
            lineEntries.add(Entry(index.toFloat(), ratio.toFloat()))

            // 处理标签文本，避免过长
            var label = errorStat.errorType
            if (label.length > 10) {
                label = label.substring(0, 10)
            }
            xLabels.add(label)
        }

        if (barEntries.isNotEmpty()) {
            // 创建柱状图数据集
            val barDataSet = com.github.mikephil.charting.data.BarDataSet(barEntries, "错误次数")
            barDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)

            // 创建折线图数据集
            val lineDataSet = LineDataSet(lineEntries, "错误占比(%)")
            lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.blue)
            lineDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.blue))
            lineDataSet.lineWidth = 2f
            lineDataSet.circleRadius = 4f
            lineDataSet.setDrawCircleHole(false)
            lineDataSet.axisDependency =
                com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT

            val barData = com.github.mikephil.charting.data.BarData(barDataSet)
            barData.barWidth = 0.4f

            val lineData = LineData(lineDataSet)

            // 创建组合数据
            val combinedData = com.github.mikephil.charting.data.CombinedData()
            combinedData.setData(barData)
            combinedData.setData(lineData)

            // 配置X轴
            val xAxis = combinedChart.xAxis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(
                        value: Float,
                        axis: com.github.mikephil.charting.components.AxisBase?
                    ): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < xLabels.size) {
                            xLabels[index]
                        } else {
                            ""
                        }
                    }
                }
                granularity = 1f
                labelRotationAngle = -45f
            }

            // 配置左侧Y轴（柱状图）
            val leftAxis = combinedChart.axisLeft
            leftAxis.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(
                        value: Float,
                        axis: com.github.mikephil.charting.components.AxisBase?
                    ): String {
                        return value.toInt().toString()
                    }
                }
            }

            // 配置右侧Y轴（折线图）
            val rightAxis = combinedChart.axisRight
            rightAxis.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 10f
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(
                        value: Float,
                        axis: com.github.mikephil.charting.components.AxisBase?
                    ): String {
                        return "${value.toInt()}%"
                    }
                }
            }

            // 应用数据
            combinedChart.data = combinedData
            combinedChart.invalidate()
        }
    }





    //
    // 将时间字符串解析为时间戳
    private fun parseTimeString(timeString: String): Long {
        return try {
            val date = timeFormat.parse(timeString)
            date?.time ?: 0L
        } catch (e: Exception) {
            Log.w("MobileErrorFragment", "Failed to parse time string: $timeString", e)
            0L
        }
    }

    // 格式化时间用于X轴显示
    private fun formatTimeForXAxis(timeString: String): String {
        return try {
            val date = timeFormat.parse(timeString)
            if (date != null) {
                // 根据需要格式化显示，例如只显示月日和时分
                val displayFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                displayFormat.format(date)
            } else {
                timeString
            }
        } catch (e: Exception) {
            Log.w("MobileErrorFragment", "Failed to format time string: $timeString", e)
            if (timeString.length > 16) {
                timeString.substring(0, 16)
            } else {
                timeString
            }
        }
    }

    private fun setupLineChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }

        // 配置X轴
        val xAxis = lineChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            // 使用自定义格式化器显示时间
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < timeStrings.size) {
                        formatTimeForXAxis(timeStrings[index])
                    } else {
                        ""
                    }
                }
            }
            granularity = 1f // 确保每个点都显示
            labelRotationAngle = -45f // 旋转标签避免重叠
        }

        // 配置左侧Y轴
        val leftAxis = lineChart.axisLeft
        leftAxis.apply {
            setDrawGridLines(true)
            axisMinimum = 0f // 从0开始
        }

        // 隐藏右侧Y轴
        lineChart.axisRight.isEnabled = false

        setData()
    }

    private fun setData() {
        if (entries.isEmpty()) {
            Log.w("MobileErrorFragment", "No data to display")
            return
        }

        val dataSet = LineDataSet(entries, "异常次数统计")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.orange))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }










}

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
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.AverageTime
import com.example.pmp.data.model.AverageTimeResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.PerformanceData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import retrofit2.Call
import retrofit2.Response

class FragmentPerformanceVM : ViewModel() {
    private val apiService = ServiceCreator.create(MyApiService::class.java)
    private lateinit var barChart: BarChart
    private lateinit var projectId: String
    private lateinit var platform: String
    private lateinit var timeType: String
    private lateinit var context: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var combinedChart: CombinedChart

    fun sendRequest(
        barChart: BarChart,
        projectId: String,
        platform: String,
        timeType: String,
        context: Context,
        progressBar: ProgressBar
    ) {
        this.progressBar = progressBar
        this.barChart = barChart
        this.projectId = projectId
        this.platform = platform
        this.timeType = timeType
        this.context = context
        progressBar.visibility = View.VISIBLE
        Log.d(
            "FragmentPerformanceVM",
            "Preparing to send request: projectId=$projectId, platform=$platform, timeType=$timeType"
        )
        apiService.getAverageTime(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            projectId,
            platform,
            timeType
        )
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
                            Log.d(
                                "FragmentPerformanceVM",
                                "Received ${averageTimes.size} data points"
                            )
                            displayDataOnChart(averageTimes)
                            progressBar.visibility = View.GONE
                        } else {
                            Log.w("FragmentPerformanceVM", "API response data is null")
                        }
                    } else {
                        Log.e(
                            "FragmentPerformanceVM",
                            "Response not successful: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<AverageTimeResponse>, t: Throwable) {
                    Log.e("FragmentPerformanceVM", "Network request failed", t)
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun displayDataOnChart(averageTimes: List<AverageTime>) {
        Log.d("FragmentPerformanceVM", "Displaying data on chart")

        // 准备数据
        val entries = ArrayList<BarEntry>()
        val apiLabels = ArrayList<String>()

        averageTimes.forEachIndexed { index, averageTime ->
            Log.d(
                "FragmentPerformanceVM",
                "Processing data point: ${averageTime.api} = ${averageTime.averageTime}"
            )

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
        if (platform.equals("frontend")) {
            dataSet.color = ContextCompat.getColor(context, R.color.qq_blue)
        } else if (platform.equals("backend")) {
            dataSet.color = ContextCompat.getColor(context, R.color.purple)
        } else {
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
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
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
            if (apiLabels.size > 15) {  // 调整标签显示密度
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
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
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
            barChart.setVisibleXRangeMaximum(15f) // 初始只显示15个数据点
        }

        // 刷新图表
        barChart.invalidate()

        Log.d("FragmentPerformanceVM", "Chart updated with ${entries.size} data points")
    }

    fun onClickDay() {
        sendRequest(barChart, projectId, platform, "day", context, progressBar)
    }

    fun onClickWeek() {
        sendRequest(barChart, projectId, platform, "week", context, progressBar)
    }

    fun onClickMonth() {
        sendRequest(barChart, projectId, platform, "month", context, progressBar)
    }

    private fun parseMemoryToMB(memoryString: String?): Float {
        if (memoryString == null) return 0f

        return try {
            when {
                memoryString.endsWith("MB") -> {
                    memoryString.replace("MB", "").trim().toFloat()
                }

                memoryString.endsWith("KB") -> {
                    memoryString.replace("KB", "").trim().toFloat() / 1024
                }

                memoryString.endsWith("GB") -> {
                    memoryString.replace("GB", "").trim().toFloat() * 1024
                }

                else -> {
                    // 默认假设是MB
                    memoryString.toFloat()
                }
            }
        } catch (e: NumberFormatException) {
            0f
        }
    }

    /**
     * 处理并显示组合图表（柱状图+折线图）
     * 柱状图显示不同operationId的FPS数据，标注在柱子上
     * 折线图显示内存使用情况（左右两个Y轴）
     */
    private fun displayCombinedPerformanceChart(rawData: List<PerformanceData>) {
        Log.d("FragmentPerformanceVM", "Displaying combined performance chart with ${rawData.size} data points")

        if (rawData.isEmpty()) {
            Log.w("FragmentPerformanceVM", "No performance data to display")
            return
        }

        // 按时间戳分组数据
        val groupedData = rawData.groupBy { it.timestamp }
        Log.d("FragmentPerformanceVM", "Grouped data into ${groupedData.size} time groups")

        // 准备X轴标签（时间戳）
        val timestamps = groupedData.keys.toList().sorted()
        val xLabels = timestamps.map { formatTimeLabel(it) }
        Log.d("FragmentPerformanceVM", "X-axis labels: $xLabels")

        // 为每个时间点创建柱状图数据
        val barEntries = mutableListOf<BarEntry>()
        val barLabels = mutableListOf<String>() // 存储每个柱子的operationId标签

        timestamps.forEachIndexed { index, timestamp ->
            val items = groupedData[timestamp] ?: emptyList()

            // 为每个数据项创建一个柱子
            items.forEachIndexed { itemIndex, item ->
                // 使用时间索引+数据项索引作为X坐标，确保每个柱子位置唯一
                val xIndex = index + itemIndex * 0.1f - (items.size - 1) * 0.05f
                val fps = item.operationFps?.toFloat() ?: 0f
                barEntries.add(BarEntry(xIndex, fps))

                // 记录operationId标签（即使是null也显示）
                val label = item.operationId ?: "null"
                barLabels.add(label)

                Log.d(
                    "FragmentPerformanceVM",
                    "Time $timestamp, Item $itemIndex - Operation: $label, FPS: $fps"
                )
            }
        }

        // 创建柱状图数据集
        val barDataSet = BarDataSet(barEntries, "FPS")
        barDataSet.color = ContextCompat.getColor(context, R.color.orange)
        barDataSet.valueTextSize = 10f

        // 设置自定义值格式化器来显示operationId
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                if (barEntry == null) return ""

                val index = barEntries.indexOfFirst { it.x == barEntry.x }
                if (index >= 0 && index < barLabels.size) {
                    // 显示格式：FPS值\n(operationId)
                    val fps = barEntry.y.toInt()
                    val operationId = barLabels[index]
                    return "$fps\n($operationId)"
                }
                return barEntry.y.toInt().toString()
            }
        }

        // 创建图表数据
        val barData = BarData(barDataSet)

        // 准备折线图数据（内存使用情况）
        val usedMemoryEntries = mutableListOf<Entry>()
        val totalMemoryEntries = mutableListOf<Entry>()

        timestamps.forEachIndexed { index, timestamp ->
            val items = groupedData[timestamp] ?: emptyList()

            // 为每个时间点的每个数据项添加内存数据点
            items.forEachIndexed { itemIndex, item ->
                if (item.memoryUsage != null) {
                    val usedMemory = parseMemoryToMB(item.memoryUsage.usedMemory)
                    val totalMemory = parseMemoryToMB(item.memoryUsage.totalMemory)

                    // 使用相同的位置索引确保折线与柱子对齐
                    val xIndex = index + itemIndex * 0.1f - (items.size - 1) * 0.05f
                    usedMemoryEntries.add(Entry(xIndex, usedMemory))
                    totalMemoryEntries.add(Entry(xIndex, totalMemory))

                    Log.d(
                        "FragmentPerformanceVM",
                        "Time $timestamp, Item $itemIndex - Used Memory: $usedMemory MB, Total Memory: $totalMemory MB"
                    )
                }
            }
        }

        // 创建折线数据集
        val usedMemoryDataSet = LineDataSet(usedMemoryEntries, "已用内存 (MB)")
        usedMemoryDataSet.color = ContextCompat.getColor(context, R.color.qq_blue) // 橙色
        usedMemoryDataSet.setCircleColor(ContextCompat.getColor(context, R.color.qq_blue))
        usedMemoryDataSet.lineWidth = 2f
        usedMemoryDataSet.circleRadius = 4f
        usedMemoryDataSet.setDrawValues(false)
        usedMemoryDataSet.axisDependency = com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT // 右侧Y轴

        val totalMemoryDataSet = LineDataSet(totalMemoryEntries, "总内存 (MB)")
        totalMemoryDataSet.color = ContextCompat.getColor(context, R.color.purple) // 紫色
        totalMemoryDataSet.setCircleColor(ContextCompat.getColor(context, R.color.purple))
        totalMemoryDataSet.lineWidth = 2f
        totalMemoryDataSet.circleRadius = 4f
        totalMemoryDataSet.setDrawValues(false)
        totalMemoryDataSet.axisDependency = com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT // 右侧Y轴

        // 创建组合图表数据
        val lineData = LineData(usedMemoryDataSet, totalMemoryDataSet)

        // 创建组合图表数据
        val combinedData = CombinedData()
        combinedData.setData(barData)
        combinedData.setData(lineData)

        // 配置组合图表
        combinedChart.apply {
            this.data = combinedData
            description.isEnabled = false
            invalidate()
            setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        // 请求父视图不要拦截触摸事件
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        // 恢复父视图对触摸事件的拦截
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                // 调用图表自身的onTouchEvent方法
                onTouchEvent(event)
            }
        }

        // 配置X轴
        val xAxis = combinedChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
                    // 找到最接近的柱子标签
                    val index = Math.round(value).toInt()
                    return if (index >= 0 && index < xLabels.size) {
                        xLabels[index]
                    } else {
                        ""
                    }
                }
            }
            granularity = 1f
            labelRotationAngle = -45f
            textSize = 8f
        }

        // 配置左侧Y轴（FPS）
        val leftAxis = combinedChart.axisLeft
        leftAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
                    return value.toInt().toString()
                }
            }
            textSize = 10f
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f
        }

        // 配置右侧Y轴（内存大小）
        val rightAxis = combinedChart.axisRight
        rightAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
                    return "${value.toInt()}MB"
                }
            }
            textSize = 10f
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f
        }

        // 刷新图表
        combinedChart.invalidate()

        Log.d("FragmentPerformanceVM", "Combined chart updated successfully")
    }


    private fun formatTimeLabel(timestamp: String): String {
        return try {
            // 从完整时间戳中提取时间和日期部分
            val dateTimeParts = timestamp.split("T")
            if (dateTimeParts.size > 1) {
                val timePart = dateTimeParts[1].split(".")[0] // 移除毫秒部分
                return timePart
            } else {
                timestamp
            }
        } catch (e: Exception) {
            timestamp
        }
    }

    /**
     * 获取移动端操作性能数据并显示在组合图表中
     */
    fun getPerformanceData(combinedChart: CombinedChart, projectId: String, timeType: String, context: Context) {
        Log.d("FragmentPerformanceVM", "Fetching performance data for project: $projectId, timeType: $timeType")

        this.combinedChart = combinedChart
        this.projectId = projectId
        this.timeType = timeType
        this.context = context

        apiService.getMobileOperationalPerformance(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            projectId,
            timeType
        )
            .enqueue(object : retrofit2.Callback<ApiResponse<List<PerformanceData>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<PerformanceData>>>,
                    response: Response<ApiResponse<List<PerformanceData>>>
                ) {
                    Log.d("FragmentPerformanceVM", "Performance data response received: ${response.isSuccessful}")

                    if (response.isSuccessful) {
                        val responseData = response.body()
                        if (responseData?.code == 200 && responseData.data != null) {
                            Log.d("FragmentPerformanceVM", "Performance data received: ${responseData.data.size} items")

                            // 直接使用 PerformanceData，无需转换
                            displayCombinedPerformanceChart(responseData.data)
                        } else {
                            Log.w("FragmentPerformanceVM", "Performance data response not valid: code=${responseData?.code}")
                        }
                    } else {
                        Log.e("FragmentPerformanceVM", "Performance data response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<PerformanceData>>>, t: Throwable) {
                    Log.e("FragmentPerformanceVM", "Failed to fetch performance data", t)
                }
            })
    }

    fun onClickDay1() {
        getPerformanceData(combinedChart, projectId, "day", context)
    }

    fun onClickWeek1() {
        getPerformanceData(combinedChart, projectId, "week", context)
    }

    fun onClickMonth1() {
        getPerformanceData(combinedChart, projectId, "month", context)
    }
}

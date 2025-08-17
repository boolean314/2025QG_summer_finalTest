package com.example.pmp.viewModel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.IpInterceptionCount
import com.example.pmp.data.model.MethodInvocationStats

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentErrorVM : ViewModel() {

    var startTime = MutableLiveData<String>()
    var endTime = MutableLiveData<String>()
    private var projectId: String? = null
    private lateinit var context: Context
    private val apiService = ServiceCreator.create(MyApiService::class.java)
    private lateinit var barChart: BarChart

    fun setData(projectId: String, context: Context, barChart: BarChart) {
        this.projectId = projectId
        this.context = context
        this.barChart = barChart
    }

    fun chooseStartTime() {
        showDateTimePicker(context, true)
    }

    fun chooseEndTime() {
        showDateTimePicker(context, false)
    }

    fun showDateTimePicker(context: Context, isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 先显示日期选择器
        var datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // 日期选择完成后显示时间选择器
                var timePickerDialog = TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        // 时间选择完成，格式化日期时间
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(
                            selectedYear,
                            selectedMonth,
                            selectedDay,
                            selectedHour,
                            selectedMinute
                        )
                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val dateTime = dateFormat.format(selectedCalendar.time)

                        // 根据 isStartTime 参数决定赋值给哪个变量
                        if (isStartTime) {
                            startTime.value = dateTime
                        } else {
                            endTime.value = dateTime
                        }
                    },
                    hour,
                    minute,
                    true
                )

                timePickerDialog.show()
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun sendRequest() {
        Log.d("FragmentErrorVM", "sendRequest: $projectId ${startTime.value} ${endTime.value}")
        if (projectId == null || startTime.value == null || endTime.value == null) {
            Toast.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show()
            return
        }
        // 使用正确的API方法和数据模型
        apiService.getIpInterceptionCount(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            projectId!!,
            startTime.value!!,
            endTime.value!!
        ).enqueue(object : retrofit2.Callback<ApiResponse<List<IpInterceptionCount>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<IpInterceptionCount>>>,
                response: Response<ApiResponse<List<IpInterceptionCount>>>
            ) {
                Log.d("FragmentErrorVM", "onResponse: $response")
                Log.d("FragmentErrorVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("FragmentErrorVM", "onResponse: ${response.body()}")
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 200) {
                        // 处理成功响应并更新图表
                        val ipInterceptionList = apiResponse.data
                        updateBarChartWithIpInterception(ipInterceptionList)
                    } else {
                        // 处理其他状态码
                        Log.e("FragmentErrorVM", "API Error: ${apiResponse?.msg}")
                    }
                } else {
                    Log.e("FragmentErrorVM", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<List<IpInterceptionCount>>>,
                t: Throwable
            ) {
                // 处理请求失败
                Log.e("FragmentErrorVM", "onFailure: $t")
            }
        })
    }

    //方法统计
    fun sendRequest2() {
        Log.d("FragmentErrorVM", "sendRequest2: $projectId ${startTime.value} ${endTime.value}")
        if (projectId == null || startTime.value == null || endTime.value == null) {
            Toast.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show()
            return
        }
        // 使用正确的API方法和数据模型
        apiService.getMethodInvocationStats(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            projectId!!,
            startTime.value!!,
            endTime.value!!
        ).enqueue(object : retrofit2.Callback<ApiResponse<List<MethodInvocationStats>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<MethodInvocationStats>>>,
                response: Response<ApiResponse<List<MethodInvocationStats>>>
            ) {
                Log.d("FragmentErrorVM", "onResponse2: $response")
                Log.d("FragmentErrorVM", "onResponse2: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("FragmentErrorVM", "onResponse2: ${response.body()}")
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 200) {
                        // 处理成功响应并更新图表
                        val methodInterceptionList = apiResponse.data
                        updateBarChartWithMethodInvocationStats(methodInterceptionList)
                    } else {
                        // 处理其他状态码
                        Log.e("FragmentErrorVM", "API Error: ${apiResponse?.msg}")
                    }
                } else {
                    Log.e("FragmentErrorVM", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<List<MethodInvocationStats>>>,
                t: Throwable
            ) {
                // 处理请求失败
                Log.e("FragmentErrorVM", "onFailure2: $t")
            }
        })
    }


    fun updateBarChartWithMethodInvocationStats(methodInterceptionList: List<MethodInvocationStats>?) {
        if (methodInterceptionList.isNullOrEmpty()) {
            // 如果没有数据，清除图表
            barChart.clear()
            return
        }

        // 准备数据
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // 遍历数据列表，创建图表数据
        for ((index, stat) in methodInterceptionList.withIndex()) {
            // 横坐标是IP地址，纵坐标是拦截次数
            entries.add(BarEntry(index.toFloat(), stat.event.toFloat()))

            // 处理IP地址标签
            var label = stat.methodName
            if (label.length > 15) {  // 缩短标签长度
                label = label.substring(0, 15) + "..."
            }
            labels.add(label)
        }

        // 创建数据集
        val dataSet = BarDataSet(entries, "方法调用统计")
        dataSet.color = ContextCompat.getColor(context, R.color.purple)
        dataSet.valueTextSize = 10f

        // 添加数值格式化器
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        // 只有当数据点非常密集时才隐藏数值标签
        if (entries.size > 20) {
            dataSet.setDrawValues(false)
        } else {
            dataSet.setDrawValues(true)
        }

        // 创建图表数据
        val data = BarData(dataSet)
        // 调整柱子宽度以适应不同数量的数据
        if (entries.size > 10) {
            data.barWidth = 0.5f
        } else {
            data.barWidth = 0.5f
        }

        // 配置图表
        barChart.apply {
            this.data = data
            description.isEnabled = false
            setFitBars(true)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            // 增加底部偏移量，确保X轴标签完整显示
            setExtraOffsets(10f, 10f, 10f, 40f) // 增加底部偏移量到40f

            // 解决与ScrollView的滑动冲突
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
                    return if (index >= 0 && index < labels.size) {
                        labels[index]
                    } else {
                        ""
                    }
                }
            }

            // 关键修改：处理标签过多的情况
            if (labels.size > 12) {  // 调整标签显示密度
                granularity = (labels.size / 12f).toFloat() // 控制标签密度
                setLabelCount(minOf(8, labels.size), false) // 最多显示8个标签
            } else {
                granularity = 1f
            }

            labelRotationAngle = -70f  // 增加倾斜角度到-70度
            textSize = 7f  // 减小字体大小
        }

        // 配置左侧Y轴
        val leftAxis = barChart.axisLeft
        leftAxis.apply {
            textSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
                    return value.toInt().toString()
                }
            }
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f // 确保Y轴从0开始
        }

        // 隐藏右侧Y轴
        barChart.axisRight.isEnabled = false

        // 设置可见的X轴范围（初始只显示一部分数据）
        if (entries.size > 12) {
            barChart.setVisibleXRangeMaximum(12f) // 初始只显示8个数据点
        }

        // 刷新图表
        barChart.invalidate()
    }

    fun updateBarChartWithIpInterception(ipInterceptionList: List<IpInterceptionCount>?) {
        if (ipInterceptionList.isNullOrEmpty()) {
            // 如果没有数据，清除图表
            barChart.clear()
            return
        }

        // 准备数据
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // 遍历数据列表，创建图表数据
        for ((index, stat) in ipInterceptionList.withIndex()) {
            // 横坐标是IP地址，纵坐标是拦截次数
            entries.add(BarEntry(index.toFloat(), stat.event.toFloat()))

            // 处理IP地址标签
            var label = stat.ip
            if (label.length > 15) {  // 缩短标签长度
                label = label.substring(0, 15) + "..."
            }
            labels.add(label)
        }

        // 创建数据集
        val dataSet = BarDataSet(entries, "IP拦截次数统计")
        dataSet.color = ContextCompat.getColor(context, R.color.purple)
        dataSet.valueTextSize = 10f

        // 添加数值格式化器
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        // 只有当数据点非常密集时才隐藏数值标签
        if (entries.size > 20) {
            dataSet.setDrawValues(false)
        } else {
            dataSet.setDrawValues(true)
        }

        // 创建图表数据
        val data = BarData(dataSet)
        // 调整柱子宽度以适应不同数量的数据
        if (entries.size > 10) {
            data.barWidth = 0.5f
        } else {
            data.barWidth = 0.5f
        }

        // 配置图表
        barChart.apply {
            this.data = data
            description.isEnabled = false
            setFitBars(true)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            // 增加底部偏移量，确保X轴标签完整显示
            setExtraOffsets(10f, 10f, 10f, 40f) // 增加底部偏移量到40f

            // 解决与ScrollView的滑动冲突
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
                    return if (index >= 0 && index < labels.size) {
                        labels[index]
                    } else {
                        ""
                    }
                }
            }

            // 关键修改：处理标签过多的情况
            if (labels.size > 12) {  // 调整标签显示密度
                granularity = (labels.size / 12f).toFloat() // 控制标签密度
                setLabelCount(minOf(8, labels.size), false) // 最多显示8个标签
            } else {
                granularity = 1f
            }

            labelRotationAngle = -70f  // 增加倾斜角度到-70度
            textSize = 7f  // 减小字体大小
        }

        // 配置左侧Y轴
        val leftAxis = barChart.axisLeft
        leftAxis.apply {
            textSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(
                    value: Float,
                    axis: com.github.mikephil.charting.components.AxisBase?
                ): String {
                    return value.toInt().toString()
                }
            }
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f // 确保Y轴从0开始
        }

        // 隐藏右侧Y轴
        barChart.axisRight.isEnabled = false

        // 设置可见的X轴范围（初始只显示一部分数据）
        if (entries.size > 12) {
            barChart.setVisibleXRangeMaximum(12f) // 初始只显示8个数据点
        }

        // 刷新图表
        barChart.invalidate()
    }
}

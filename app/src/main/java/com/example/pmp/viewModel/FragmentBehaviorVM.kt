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
import com.example.pmp.data.model.ManualTrackingStats
import com.github.mikephil.charting.charts.BarChart
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentBehaviorVM: ViewModel() {
    var startTime=MutableLiveData<String>()
    var endTime=MutableLiveData<String>()
    private var projectId:String?=null
    private lateinit var context: Context
    private val apiService=ServiceCreator.create(MyApiService::class.java)
    private lateinit var barChart: BarChart

    fun setData(projectId: String,context: Context,barChart: BarChart) {
        this.projectId = projectId
        this.context=context
        this.barChart=barChart
    }
   fun chooseStartTime(){
        showDateTimePicker(context,true)
   }
fun chooseEndTime(){
        showDateTimePicker(context,false)
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
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
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

    fun sendRequest(){
        if (projectId==null||startTime.value==null||endTime.value==null){
            Toast.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("FragmentBehaviorVM", "sendRequest: $projectId ${startTime.value} ${endTime.value}")
        apiService.getManualTrackingStats("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId!!,startTime.value!!,endTime.value!!).enqueue(object:retrofit2.Callback<ApiResponse<List<ManualTrackingStats>>>{
            override fun onResponse(
                call: Call<ApiResponse<List<ManualTrackingStats>>>,
                response: Response<ApiResponse<List<ManualTrackingStats>>>
            ) {
                Log.d("FragmentBehaviorVM", "onResponse: $response")
                Log.d("FragmentBehaviorVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("FragmentBehaviorVM", "onResponse: ${response.body()}")
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 200) {
                        // 处理成功响应并更新图表
                        val manualTrackingStatsList = apiResponse.data
                        updateBarChart(manualTrackingStatsList) // 添加这一行来更新图表
                    } else {
                        // 处理其他状态码
                    }
                } else {
                    Log.e("FragmentBehaviorVM", "onResponse: ${response.code()}")
                }
            }


            override fun onFailure(call: Call<ApiResponse<List<ManualTrackingStats>>>, t: Throwable) {
                // 处理请求失败
                Log.e("FragmentBehaviorVM", "onFailure: $t")
            }
        })
    }

    fun updateBarChart(manualTrackingStatsList: List<ManualTrackingStats>?) {
        if (manualTrackingStatsList.isNullOrEmpty()) {
            // 如果没有数据，清除图表
            barChart.clear()
            return
        }

        // 准备数据
        val entries = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        val labels = ArrayList<String>()

        // 遍历数据列表，创建图表数据
        for ((index, stat) in manualTrackingStatsList.withIndex()) {
            entries.add(com.github.mikephil.charting.data.BarEntry(index.toFloat(), stat.value.toFloat()))

            // 处理标签名称，避免过长
            var label = stat.label
            if (label.length > 50) {
                label = label.substring(0, 50) + "..."
            }
            labels.add(label)
        }

        // 创建数据集
        val dataSet = com.github.mikephil.charting.data.BarDataSet(entries, "统计信息")
        dataSet.color = ContextCompat.getColor(context, R.color.qq_blue)
        dataSet.valueTextSize = 10f

        // 创建图表数据
        val data = com.github.mikephil.charting.data.BarData(dataSet)

        // 配置图表
        barChart.apply {
            this.data = data
            description.isEnabled = false
            setFitBars(true)
            invalidate()
        }

        // 配置X轴
        val xAxis = barChart.xAxis
        xAxis.apply {
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < labels.size) {
                        labels[index]
                    } else {
                        ""
                    }
                }
            }
            granularity = 1f
            labelRotationAngle = -45f
            textSize = 8f
        }

        // 配置左侧Y轴
        val leftAxis = barChart.axisLeft
        leftAxis.apply {
            textSize = 10f
            spaceBottom = 0f
            spaceTop = 0f
            axisMinimum = 0f // 确保Y轴从0开始
        }

        // 隐藏右侧Y轴
        barChart.axisRight.isEnabled = false

        // 刷新图表
        barChart.invalidate()
    }
}
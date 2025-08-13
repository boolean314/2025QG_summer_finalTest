package com.example.pmp.viewModel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.ManualTrackingStats
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

    fun setData(projectId: String,context: Context) {
        this.projectId = projectId
        this.context=context
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
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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
        Log.d("FragmentBehaviorVM", "sendRequest: $projectId ${startTime.value} ${endTime.value}")
        apiService.getManualTrackingStats(projectId!!,startTime.value!!,endTime.value!!).enqueue(object:retrofit2.Callback<ApiResponse<List<ManualTrackingStats>>>{
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
                        // 处理成功响应
                        val manualTrackingStatsList = apiResponse.data
                        // 在这里更新 UI 或执行其他操作
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
}
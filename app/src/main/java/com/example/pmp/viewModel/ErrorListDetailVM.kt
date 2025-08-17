package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.FrontErrorData
import com.example.pmp.data.model.GlobalData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ErrorListDetailVM : ViewModel() {
    private lateinit var projectId: String
    private lateinit var platform: String
    private val apiService = ServiceCreator.create(MyApiService::class.java)

    fun setData(projectId: String, platform: String) {
        this.projectId = projectId
        this.platform = platform
        // 设置数据后获取错误列表
        fun setData(projectId: String, platform: String) {
            this.projectId = projectId
            this.platform = platform
            // 设置数据后获取错误列表
            when (platform) {
                "frontend" -> getFrontendError()
                "backend" -> getBackendError()
                "mobile" -> getMobileError()
                else -> getFrontendError() // 默认获取前端错误
            }
        }
    }

    fun getFrontendError() {

        // 调用API获取前端错误列表
        apiService.getFrontendErrorList("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, platform).enqueue(object : Callback<ApiResponse<List<FrontErrorData>>> {

            override fun onResponse(
                call: Call<ApiResponse<List<FrontErrorData>>>,
                response: Response<ApiResponse<List<FrontErrorData>>>
            ) {
                Log.d("ErrorListDetailVM", "getFrontendError: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorListDetailVM", "getFrontendError: ${response.body()?.data}")

                    }
                }


            override fun onFailure(call: Call<ApiResponse<List<FrontErrorData>>>, t: Throwable) {
                Log.e("ErrorListDetailVM", "getFrontendError: ${t.message}")

        }})
    }
    }

    // 如果需要获取后端错误列表，可以添加类似方法
    fun getBackendError() {
        return
        // TODO: 实现获取后端错误列表的逻辑
    }
    fun getMobileError() {
        return
    }


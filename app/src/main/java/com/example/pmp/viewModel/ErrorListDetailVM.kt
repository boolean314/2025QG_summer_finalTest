package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.FrontendErrorData
import com.example.pmp.data.model.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ErrorListDetailVM : ViewModel() {
    private lateinit var projectId: String
    private lateinit var platform: String
    private val apiService = ServiceCreator.create(MyApiService::class.java)

    // 用于存储错误列表数据
    private val _errorList = MutableLiveData<List<FrontendErrorData>>(emptyList())
    val errorList: LiveData<List<FrontendErrorData>> = _errorList

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getPlatform(): String {
        return platform
    }
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

    fun getFrontendError() {
        Log.d("ErrorListDetailVM", "getFrontendError: projectId: $projectId, platform: $platform")
        _loading.value = true

        // 调用API获取前端错误列表
        apiService.getFrontendErrorList("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, platform)
            .enqueue(object : Callback<ApiResponse<List<List<FrontendErrorData>>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<List<FrontendErrorData>>>>,
                    response: Response<ApiResponse<List<List<FrontendErrorData>>>>
                ) {
                    _loading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("ErrorListDetailVM", "getFrontendError response: $body")

                        if (body != null && body.code == 200) {
                            // 提取实际的错误数据（第二个列表）
                            val actualErrorList = body.data.getOrNull(1) ?: emptyList()
                            _errorList.value = actualErrorList
                            Log.d("ErrorListDetailVM", "getFrontendError data count: ${actualErrorList.size}")
                        } else {
                            Log.e("ErrorListDetailVM", "API returned error: ${body?.msg}")
                        }
                    } else {
                        Log.e("ErrorListDetailVM", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<List<FrontendErrorData>>>>, t: Throwable) {
                    _loading.value = false
                    Log.e("ErrorListDetailVM", "getFrontendError onFailure: ${t.message}", t)
                }
            })
    }

    // 如果需要获取后端错误列表，可以添加类似方法
    fun getBackendError() {
        // TODO: 实现获取后端错误列表的逻辑
        return
    }

    fun getMobileError() {
        return
    }
}

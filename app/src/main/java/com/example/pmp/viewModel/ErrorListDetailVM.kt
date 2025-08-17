package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.BackendErrorData
import com.example.pmp.data.model.FrontendErrorData
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.MobileErrorData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ErrorListDetailVM : ViewModel() {
    private lateinit var projectId: String
    private lateinit var platform: String
    private val apiService = ServiceCreator.create(MyApiService::class.java)

    // 用于存储前端错误列表数据
    private val _errorList = MutableLiveData<List<FrontendErrorData>>(emptyList())
    val errorList: LiveData<List<FrontendErrorData>> = _errorList
//储存后端
    private val _errorListBackend = MutableLiveData<List<BackendErrorData>>(emptyList())
    val errorListBackend: LiveData<List<BackendErrorData>> = _errorListBackend

    //储存移动
    private val _errorListMobile = MutableLiveData<List<MobileErrorData>>(emptyList())
    val errorListMobile: LiveData<List<MobileErrorData>> = _errorListMobile

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

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

   //后端
    fun getBackendError() {
        Log.d("ErrorListDetailVM", "getBackendError: projectId: $projectId, platform: $platform")
        _loading.value = true

        apiService.getBackendErrorList("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, platform)
            .enqueue(object : Callback<ApiResponse<List<List<BackendErrorData>>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<List<BackendErrorData>>>>,
                    response: Response<ApiResponse<List<List<BackendErrorData>>>>
                ) {
                    _loading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("ErrorListDetailVM", "getFrontendError response: $body")

                        if (body != null && body.code == 200) {
                            // 提取实际的错误数据（第一个列表）
                            val actualErrorList = body.data.getOrNull(0) ?: emptyList()
                            _errorListBackend.value = actualErrorList
                            Log.d("ErrorListDetailVM", "getBackendError data count: ${actualErrorList.size}")
                        } else {
                            Log.e("ErrorListDetailVM", "API returned error: ${body?.msg}")
                        }
                    } else {
                        Log.e("ErrorListDetailVM", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<List<BackendErrorData>>>>, t: Throwable) {
                    _loading.value = false
                    Log.e("ErrorListDetailVM", "getBackendError onFailure: ${t.message}", t)
                }
            })
    }

    //移动端
    fun getMobileError() { Log.d("ErrorListDetailVM", "getMobileError: projectId: $projectId, platform: $platform")
        _loading.value = true

        // 调用API获取前端错误列表
        apiService.getMobileErrorList("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, platform)
            .enqueue(object : Callback<ApiResponse<List<List<MobileErrorData>>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<List<MobileErrorData>>>>,
                    response: Response<ApiResponse<List<List<MobileErrorData>>>>
                ) {
                    _loading.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("ErrorListDetailVM", "getMobileError response: $body")

                        if (body != null && body.code == 200) {
                            // 提取实际的错误数据（第三个列表）
                            val actualErrorList = body.data.getOrNull(2) ?: emptyList()
                            _errorListMobile.value = actualErrorList
                            Log.d("ErrorListDetailVM", "getMobileError data count: ${actualErrorList.size}")
                        } else {
                            Log.e("ErrorListDetailVM", "API returned error: ${body?.msg}")
                        }
                    } else {
                        Log.e("ErrorListDetailVM", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<List<MobileErrorData>>>>, t: Throwable) {
                    _loading.value = false
                    Log.e("ErrorListDetailVM", "getMobileError onFailure: ${t.message}", t)
                }
            })
    }
}

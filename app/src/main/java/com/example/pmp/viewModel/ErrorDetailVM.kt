package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.AssignMemberData
import com.example.pmp.data.model.BackendErrorData
import com.example.pmp.data.model.FrontendErrorData
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.HandleStatusData
import com.example.pmp.data.model.MobileErrorData
import com.example.pmp.data.model.ThresholdData
import com.example.pmp.data.model.UpdateHandleStatusData
import retrofit2.Call
import retrofit2.Response

class ErrorDetailVM : ViewModel() {
    var timestamp = MutableLiveData<String?>()
    var errorId = MutableLiveData<Int?>()
    var errorType = MutableLiveData<String?>()
    var message = MutableLiveData<String?>()
    var userAgent = MutableLiveData<String?>()
    var fileName = MutableLiveData<String?>()
    var lineNumber = MutableLiveData<Int?>()
    var colNumber = MutableLiveData<Int?>()

    var requestUrl = MutableLiveData<String?>()
    var requestMethod = MutableLiveData<String?>()
    var requestStatusCode = MutableLiveData<Int?>()
    var requestStackText = MutableLiveData<String?>()
    var environment = MutableLiveData<String?>()
    val projectId = MutableLiveData<String?>()
    var oldThreshold = MutableLiveData<Int?>()
    var handleStatus = MutableLiveData<Int?>()
    var platform = MutableLiveData<String?>()
    private val apiService = ServiceCreator.create(MyApiService::class.java)


    fun getFrontendErrorDetail(errorId1: Int, platform: String) {
        apiService.getFrontendErrorDetail(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            errorId1,
            platform
        ).enqueue(object : retrofit2.Callback<ApiResponse<FrontendErrorData>> {
            override fun onResponse(
                call: Call<ApiResponse<FrontendErrorData>>,
                response: Response<ApiResponse<FrontendErrorData>>
            ) {
                Log.d("ErrorDetailVM", "onResponse: $response")
                Log.d("ErrorDetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "onResponse: ${response.body()?.data}")
                    val time = response.body()?.data?.timestamp?.replace("T", " ")?.substring(0, 16)
                    timestamp.value = time
                    errorId.value = response.body()?.data?.id
                    errorType.value = response.body()?.data?.errorType
                    message.value = response.body()?.data?.message ?: ""
                    userAgent.value = response.body()?.data?.userAgent ?: ""
                    fileName.value = response.body()?.data?.jsFilename ?: ""
                    lineNumber.value = response.body()?.data?.lineno ?: 0
                    colNumber.value = response.body()?.data?.colno ?: 0
                    requestUrl.value = response.body()?.data?.request?.url ?: ""
                    requestMethod.value = response.body()?.data?.request?.method ?: ""
                    requestStatusCode.value = response.body()?.data?.response?.status ?: 0
                    requestStackText.value = response.body()?.data?.stack ?: ""
                    environment.value = ""
                    projectId.value = response.body()?.data?.projectId ?: ""

                }
            }

            override fun onFailure(call: Call<ApiResponse<FrontendErrorData>>, t: Throwable) {
                Log.d("ErrorDetailVM", "onFailure: $t")
            }
        })
    }

    fun getBackendErrorDetail(errorId1: Int, platform: String) {

        apiService.getBackendErrorDetail(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            errorId1,
            platform
        ).enqueue(object : retrofit2.Callback<ApiResponse<BackendErrorData>> {
            override fun onResponse(
                call: Call<ApiResponse<BackendErrorData>>,
                response: Response<ApiResponse<BackendErrorData>>
            ) {
                Log.d("ErrorDetailVM", "onResponse: $response")
                Log.d("ErrorDetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "onResponse: ${response.body()?.data}")
                    val time = response.body()?.data?.timestamp?.replace("T", " ")?.substring(0, 16)
                    timestamp.value = time
                    errorId.value = response.body()?.data?.id
                    errorType.value = response.body()?.data?.errorType
                    requestStackText.value = response.body()?.data?.stack ?: ""
                    environment.value = response.body()?.data?.environment ?: ""
                    message.value = ""
                    userAgent.value = ""
                    fileName.value = ""
                    lineNumber.value = 0
                    colNumber.value = 0
                    requestUrl.value = ""
                    requestMethod.value = ""
                    requestStatusCode.value = 0
                    projectId.value = response.body()?.data?.projectId ?: ""

                }
            }

            override fun onFailure(call: Call<ApiResponse<BackendErrorData>>, t: Throwable) {
                Log.d("ErrorDetailVM", "onFailure: $t")
            }
        })


    }

    fun getMobileErrorDetail(errorId1: Int, platform: String) {
        apiService.getMobileErrorDetail(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            errorId1,
            platform
        ).enqueue(object : retrofit2.Callback<ApiResponse<MobileErrorData>> {
            override fun onResponse(
                call: Call<ApiResponse<MobileErrorData>>,
                response: Response<ApiResponse<MobileErrorData>>
            ) {
                Log.d("ErrorDetailVM", "onResponse: $response")
                Log.d("ErrorDetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "onResponse: ${response.body()?.data}")
                    val time = response.body()?.data?.timestamp?.replace("T", " ")?.substring(0, 16)
                    timestamp.value = time
                    errorId.value = response.body()?.data?.id
                    errorType.value = response.body()?.data?.errorType
                    requestStackText.value = response.body()?.data?.stack ?: ""
                    message.value = response.body()?.data?.message ?: ""
                    userAgent.value = ""
                    fileName.value = ""
                    lineNumber.value = 0
                    colNumber.value = 0
                    requestUrl.value = ""
                    requestMethod.value = ""
                    requestStatusCode.value = 0
                    environment.value = ""
                    projectId.value = response.body()?.data?.projectId ?: ""

                }
            }

            override fun onFailure(call: Call<ApiResponse<MobileErrorData>>, t: Throwable) {
                Log.d("ErrorDetailVM", "onFailure: $t")
            }
        })
    }


    //获取项目中的阈值
    fun getThreshold(platform: String) {

        apiService.getThreshold(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            errorType.value!!,
            projectId.value!!,
            platform
        ).enqueue(object : retrofit2.Callback<ApiResponse<ThresholdData>> {
            override fun onResponse(
                call: Call<ApiResponse<ThresholdData>>,
                response: Response<ApiResponse<ThresholdData>>
            ) {
                Log.d("ErrorDetailVM", "onResponse: $response")
                Log.d("ErrorDetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "onResponse: ${response.body()?.data}")
                    val threshold = response.body()?.data?.threshold ?: 1
                    oldThreshold.value = threshold
                    Log.d("ErrorDetailVM", "onResponse: $threshold")
                    Log.d("ErrorDetailVM", "onResponse: ${oldThreshold.value}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<ThresholdData>>, t: Throwable) {
                Log.d("ErrorDetailVM", "onFailure: $t")
            }
        })
    }

    //修改阈值，四个参数
    fun updateThreshold(platform: String, threshold: Int) {
        Log.d("ErrorDetailVM", "updateThreshold: $threshold")
        Log.d("ErrorDetailVM", "updateThreshold: ${errorType.value}")
        Log.d("ErrorDetailVM", "updateThreshold: ${projectId.value}")
        Log.d("ErrorDetailVM", "updateThreshold: $platform")
        Log.d("ErrorDetailVM", "updateThreshold: ${environment.value}")
        apiService.updateThreshold(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            ThresholdData(errorType.value!!, environment.value!!, projectId.value!!, platform,threshold,),

        ).enqueue(object : retrofit2.Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                Log.d("ErrorDetailVM", "updateThreshold onResponse: $threshold")
                Log.d("ErrorDetailVM", "updateThreshold onResponse: $response")
                Log.d("ErrorDetailVM", "updateThreshold onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "updateThreshold onResponse: ${response.body()?.msg}")
                    oldThreshold.value = threshold
                }
            }
            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.d("ErrorDetailVM", "onFailure: $t")
            }
        })

        }
    //获取错误的解决状态
    fun getHandleStatus(platform1: String) {
        platform.value = platform1
        apiService.getHandleStatus(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            projectId.value!!,
            errorType.value!!,
            platform1
        ).enqueue(object : retrofit2.Callback<ApiResponse<HandleStatusData>> {
            override fun onResponse(
                call: Call<ApiResponse<HandleStatusData>>,
                response: Response<ApiResponse<HandleStatusData>>
            ) {
                Log.d("ErrorDetailVM", "HandleStatus_onResponse: $response")
                Log.d("ErrorDetailVM", "HandleStatus_onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "HandleStatus_onResponse: ${response.body()?.data}")
                    Log.d("ErrorDetailVM", "HandleStatus_onResponse: ${response.body()?.data?.isHandle}")
                    handleStatus.value = response.body()?.data?.isHandle ?: 0
                }
            }

            override fun onFailure(call: Call<ApiResponse<HandleStatusData>>, t: Throwable) {
                Log.d("ErrorDetailVM", "HandleStatus_onFailure: $t")
            }
        })
    }


    //更新错误的解决状态
    fun updateHandleStatus() {
        apiService.updateHandleStatus(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            UpdateHandleStatusData(errorType.value!!, platform.value!!, projectId.value!!),
        ).enqueue(object : retrofit2.Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {

                Log.d("ErrorDetailVM", "updateHandleStatus onResponse: $response")
                Log.d("ErrorDetailVM", "updateHandleStatus onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "updateHandleStatus onResponse: ${response.body()?.msg}")
                        if (handleStatus.value==1){
                            handleStatus.value = 0
                        }else{
                            handleStatus.value = 1
                        }
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.d("ErrorDetailVM", "updateHandleStatus onFailure: $t")
            }
        })
    }


    //把错误指派给成员
    fun assignMember(responsibleId:Long){

        Log.d("ErrorDetailVM", "delegatorId: ${GlobalData.userInfo?.id}")
        Log.d("ErrorDetailVM", "errorId: ${errorId.value}")
        Log.d("ErrorDetailVM", "platform: ${platform.value}")
        Log.d("ErrorDetailVM", "projectId: ${projectId.value}")
        Log.d("ErrorDetailVM", "responsibleId: $responsibleId")

        apiService.assignMember(
            "Bearer ${GlobalData.token}",
            GlobalData.Rsakey,
            AssignMemberData(
                GlobalData.userInfo?.id?:0,
                errorId.value!!,
                platform.value!!,
                projectId.value!!,
                responsibleId
            )
        ).enqueue(object : retrofit2.Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                Log.d("ErrorDetailVM", "assignMember onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    Log.d("ErrorDetailVM", "assignMember onResponse: ${response.body()?.msg}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.d("ErrorDetailVM", "assignMember onFailure: $t")
            }
        })
    }
}

package com.example.pmp.viewModel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.CreateProjectData
import com.example.pmp.data.model.GlobalData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import java.net.IDN

class CreateProjectVM: ViewModel() {
    private val apiService=ServiceCreator.create(MyApiService::class.java)
 val name= MutableLiveData<String>()
 val description=MutableLiveData<String>()
 val projectPermission=MutableLiveData<String>()
private  var userId:Long= GlobalData.userInfo?.id?:27
//0：老板，1：管理员，2：成员

    // 添加这个 LiveData 来通知 Activity 请求完成
    private val _projectCreated = MutableLiveData<Boolean>()
    val projectCreated: LiveData<Boolean> = _projectCreated
    fun createProject(){
        val projectName=name.value
        val projectDescription=description.value
        val isPublic: Boolean = if (projectPermission.value == "公开") true else false
        Log.d("CreateProjectVM", "createProject: 项目名称：$projectName ，项目描述：$projectDescription ，项目权限：$isPublic")
        val createProjectData= CreateProjectData(name=projectName!!,description=projectDescription!!,isPublic=isPublic,userId=userId)
        apiService.createProject(createProjectData).enqueue(object : retrofit2.Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                Log.d("CreateProjectVM", "onResponse: $response")
                Log.d("CreateProjectVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    // 项目创建成功，更新 LiveData 通知 Activity
                    _projectCreated.value = true
                } else {
                    // 项目创建失败，根据需要处理
                }
                _projectCreated.value = false


            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.d("CreateProjectVM", "onFailure: $t")
            }
        })
    }


}
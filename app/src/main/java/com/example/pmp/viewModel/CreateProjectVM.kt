package com.example.pmp.viewModel

import android.util.Log
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

private  var useId: Int= GlobalData.userInfo?.id?:27
private var power:Int=2
    private  var userRole:Int=1//0：老板，1：管理员，2：成员
    fun createProject(){
        val projectName=name.value
        val projectDescription=description.value
        val isPublic: Boolean = if (projectPermission.value == "公开") true else false
        Log.d("CreateProjectVM", "createProject: 项目名称：$projectName ，项目描述：$projectDescription ，项目权限：$isPublic")
        val createProjectData= CreateProjectData(name=projectName!!,description=projectDescription!!,isPublic=isPublic,userId=useId,power=power,userRole=userRole)
        apiService.createProject(createProjectData).enqueue(object : retrofit2.Callback<ApiResponse<Body>>{
            override fun onResponse(
                call: Call<ApiResponse<Body>>,
                response: Response<ApiResponse<Body>>
            ) {
                Log.d("CreateProjectVM", "onResponse: $response")
                Log.d("CreateProjectVM", "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<ApiResponse<Body>>, t: Throwable) {
                Log.d("CreateProjectVM", "onFailure: $t")
            }
        })
    }


}
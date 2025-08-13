package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import java.net.IDN

class CreateProjectVM: ViewModel() {
    private val apiService=ServiceCreator.create(MyApiService::class.java)
 val projectName= MutableLiveData<String>()
 val projectSummary=MutableLiveData<String>()
 val projectType=MutableLiveData<String>()
 val projectPermission=MutableLiveData<String>()
private  var id:String?=null
private  var useIDN: Int=0
private var power:Int=1
    private  var userRole:Int=1
    fun createProject(){
        val projectName=projectName.value
        val projectSummary=projectSummary.value
        val projectType=projectType.value
        val projectPermission=projectPermission.value
        Log.d("CreateProjectVM", "createProject: $projectName $projectSummary $projectType $projectPermission")
    }


}
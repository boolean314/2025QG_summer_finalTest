package com.example.pmp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FrontendDetailVM : ViewModel() {
    var projectId= MutableLiveData<String>()
    var projectName= MutableLiveData<String>()
    var projectPermission= MutableLiveData<String>()

    fun setProjectData(id:String,name:String,permission:String){
        projectId.value=id
        projectName.value=name
        projectPermission.value=permission
    }

}
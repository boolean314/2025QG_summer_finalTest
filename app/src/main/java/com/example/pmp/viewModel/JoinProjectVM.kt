package com.example.pmp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.GlobalData

class JoinProjectVM:ViewModel() {
    private val apiService= ServiceCreator.create(MyApiService::class.java)
    private val userId= GlobalData.userInfo?.id?:27
    var invitedCode=MutableLiveData<String>()


}
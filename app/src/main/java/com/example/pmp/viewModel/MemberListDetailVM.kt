package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.MemberListData

class MemberListDetailVM : ViewModel() {
    private val apiService = ServiceCreator.create(MyApiService::class.java)
    private lateinit var projectId: String

    private val _memberList = MutableLiveData<List<MemberListData>>()
    val memberList: LiveData<List<MemberListData>> = _memberList


    fun setData(projectId:String){
        this.projectId=projectId
    }

    fun getMemberList() {
        apiService.getMemberList(projectId).enqueue(object : retrofit2.Callback<ApiResponse<List<MemberListData>>> {
            override fun onResponse(
                call: retrofit2.Call<ApiResponse<List<MemberListData>>>,
                response: retrofit2.Response<ApiResponse<List<MemberListData>>>
            ) {
                Log.d("MemberListDetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val sortedList = response.body()?.data?.sortedWith(compareBy { it.userRole }) ?: emptyList()
                    _memberList.value = sortedList
                    Log.d("MemberListDetailVM", "onResponse: ${response.body()?.data}")
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse<List<MemberListData>>>, t: Throwable) {
                Log.d("MemberListDetailVM", "onFailure: ${t.message}")
            }
        })
        Log.d("MemberListDetailVM", "getMemberList: $memberList")
    }
}
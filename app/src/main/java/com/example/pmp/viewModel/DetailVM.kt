package com.example.pmp.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.ProjectDetail
import retrofit2.Call
import retrofit2.Response
import java.util.UUID

class DetailVM: ViewModel() {

    private var userId:Long=GlobalData.userInfo?.id?:0

private val apiService=ServiceCreator.create(MyApiService::class.java)
     var uuid=MutableLiveData< String>()
    var userRole=MutableLiveData<Int>()
    var name=MutableLiveData<String>()
    var isPublic=MutableLiveData<Boolean>()
    var webhook=MutableLiveData<String>()
    var inviteCode=MutableLiveData<String?>()


    fun setData(uuid:String,userId:Long,userRole:Int){
        this.uuid.value=uuid
        this.userId=userId
        this.userRole.value=userRole
        getProjectDetail()
    }


    //发送网络请求获取详细信息
    fun getProjectDetail(){
        apiService.getProjectDetail(uuid.value!!).enqueue(object : retrofit2.Callback<ApiResponse<ProjectDetail>>{
            override fun onResponse(
                call: Call<ApiResponse<ProjectDetail>>,
                response: Response<ApiResponse<ProjectDetail>>
            ) {
                Log.d("DetailVM", "onResponse: $response")
                Log.d("DetailVM", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val projectDetail = response.body()?.data
                    name.value=projectDetail?.name
                    isPublic.value=projectDetail?.isPublic
                    webhook.value=projectDetail?.webhook
                    inviteCode.value=projectDetail?.inviteCode

                    Log.d("DetailVM", "onResponse1: ${projectDetail?.name}")
                    Log.d("DetailVM", "onResponse1: ${projectDetail?.isPublic}")
                    Log.d("DetailVM", "onResponse1: ${projectDetail?.webhook}")
                    Log.d("DetailVM", "onResponse1: ${projectDetail?.inviteCode}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<ProjectDetail>>, t: Throwable) {
                Log.d("DetailVM", "onFailure: $t")
            }
        })
        getNewInviteCode()

    }


    //刷新获得最新邀请码
    fun getNewInviteCode(){
        apiService.getInviteCode(uuid.value!!).enqueue(object : retrofit2.Callback<ApiResponse<String>>{
            override fun onResponse(
                call: Call<ApiResponse<String>>,
                response: Response<ApiResponse<String>>
            ) {
                Log.d("DetailVM", "onResponse2: $response")
                Log.d("DetailVM", "onResponse2: ${response.body()}")
                if (response.isSuccessful) {
                    val newInviteCode = response.body()?.data
                    Log.d("DetailVM", "onResponse2: $newInviteCode")
                    inviteCode.value=newInviteCode
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                Log.d("DetailVM", "onFailure: $t")
            }
        })
    }
    // 复制邀请码到剪切板
    fun copyInviteCode(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Invite Code", inviteCode.value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "邀请码已复制到剪切板", Toast.LENGTH_SHORT).show()
    }

    // 复制webhook到剪切板
    fun copyWebhook(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Webhook", webhook.value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Webhook已复制到剪切板", Toast.LENGTH_SHORT).show()
    }








}
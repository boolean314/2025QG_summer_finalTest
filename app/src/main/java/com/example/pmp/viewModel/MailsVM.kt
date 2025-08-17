package com.example.pmp.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.ListMails
import com.example.pmp.data.model.MissionYes
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class MailsVM : ViewModel() {

    private val _mails = MutableLiveData<List<ListMails>>()
    val mails: LiveData<List<ListMails>> = _mails
    private var allMails: List<ListMails> = emptyList()

    fun loadMails() {
        viewModelScope.launch {
            val response = RetrofitClient.instance.loadMailsAndMissions("Bearer ${GlobalData.token}", GlobalData.Rsakey,
                GlobalData.userInfo?.id,0)
            if (response.code == 200) {
                val data = response.data as? List<Map<String, Any>>
                allMails = data?.map { map ->
                    val projectId = map["projectId"] as? String ?: ""
                    val platform = map["platform"] as? String ?: ""
                    val errorType = map["errorType"] as? String ?: ""
                    val statusResp = RetrofitClient.instance.updateStatus(
                        "Bearer ${GlobalData.token}", GlobalData.Rsakey,
                        projectId, errorType, platform
                    )
                    val isHandled = statusResp.code == 200
                    ListMails(
                        errorId = (map["errorId"] as? Number)?.toInt(),
                        errorMessage = map["errorMessage"] as? String?:"",
                        errorType = map["errorType"] as? String? ?: "",
                        id = (map["id"] as? Number)?.toInt(),
                        platform = platform,
                        projectId = projectId,
                        projectName = map["projectName"] as? String? ?: "",
                        responsibleName = map["responsibleName"] as? String? ?: "",
                        senderAvatar = map["senderAvatar"] as? String? ?: "",
                        senderName = map["senderName"] as? String? ?: "",
                        receiverId = (map["receiverId"] as? Number)?.toInt(),
                        isHandled = isHandled
                    )
                } ?: emptyList()
                Log.d("MailsVM", "Mapped projects: $allMails")
                _mails.value = allMails
            }
        }
    }

    fun deleteAllMails(context: Context) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.deleteAll("Bearer ${GlobalData.token}", GlobalData.Rsakey,
                GlobalData.userInfo?.id, 1)
            if (response.code == 200) {
                Log.d("MailsVM", "All mails deleted successfully")
                allMails = emptyList()
                _mails.value = allMails
                Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
            } else {
                Log.e("MailsVM", "Failed to delete mails: ${response.msg}")
            }
        }
    }

    fun addMails(mail: ListMails) {
        val updatedList = _mails.value.orEmpty().toMutableList()
        updatedList.add(0, mail)
        _mails.postValue(updatedList)
    }

    fun deleteSingleMail(id: Int?) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.deleteMailById("Bearer ${GlobalData.token}", GlobalData.Rsakey, id)
            if (response.code == 200) {
                Log.d("MailsVM", "Mail with ID $id deleted successfully")
                val updatedList = _mails.value.orEmpty().toMutableList()
                updatedList.removeAll { it.id == id }
                _mails.postValue(updatedList)
            } else {
                Log.e("MailsVM", "Failed to delete mail with ID $id: ${response.msg}")
            }
        }
    }

    fun updateHandleStatus(context: Context, projectId: String, platform: String, errorType: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val missionYes = MissionYes(projectId, platform, errorType)
            Log.d("updateHandleStatus", "发送给服务端的数据: $missionYes")
            val response = RetrofitClient.instance.missionPassed(
                "Bearer ${GlobalData.token}",
                GlobalData.Rsakey,
                missionYes
            )
            if (response.code == 200) {
                Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                callback(true)
            } else {
                callback(true)
            }
        }
    }
}
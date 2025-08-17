package com.example.pmp.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.ListMissions
import com.example.pmp.data.model.MissionYes
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.code

class MissionsVM : ViewModel() {

    private val _missions = MutableLiveData<List<ListMissions>>()
    val missions: LiveData<List<ListMissions>> = _missions
    private var allMissions: List<ListMissions> = emptyList()

    fun loadMissions() {
        viewModelScope.launch {
            val response = RetrofitClient.instance.loadMailsAndMissions("Bearer ${GlobalData.token}", GlobalData.Rsakey,
                GlobalData.userInfo?.id,1)
            if (response.code == 200) {
                val data = response.data as? List<Map<String, Any>>
                allMissions = data?.map { map ->
                    val projectId = map["projectId"] as? String ?: ""
                    val platform = map["platform"] as? String ?: ""
                    val errorType = map["errorType"] as? String ?: ""
                    val statusResp = RetrofitClient.instance.updateStatus(
                        "Bearer ${GlobalData.token}", GlobalData.Rsakey,
                        projectId, errorType, platform
                    )
                    val isHandled = statusResp.code == 200
                    ListMissions(
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
                        responsibleId = (map["responsibleId"] as? Number)?.toInt(),
                        isHandled = isHandled
                    )
                } ?: emptyList()
                Log.d("MissionsVM", "Mapped projects: $allMissions")
                _missions.value = allMissions
            }
        }
    }

    fun deleteAllMissions(context: Context) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.deleteAll("Bearer ${GlobalData.token}", GlobalData.Rsakey,
                GlobalData.userInfo?.id, 1)
            if (response.code == 200) {
                Log.d("MissionsVM", "All missions deleted successfully")
                allMissions = emptyList()
                _missions.value = allMissions
                Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
            } else {
                Log.e("MissionsVM", "Failed to delete missions: ${response.msg}")
            }
        }
    }

    fun addMission(mission: ListMissions) {
        val updatedList = _missions.value.orEmpty().toMutableList()
        updatedList.add(0, mission)
        _missions.postValue(updatedList)
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
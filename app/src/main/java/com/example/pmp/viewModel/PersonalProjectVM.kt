package com.example.pmp.viewModel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.data.model.UserAuthentication
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class PersonalProjectVM: ViewModel() {

    private val _projects = MutableLiveData<List<PersonalProject>>()
    val projects: LiveData<List<PersonalProject>> = _projects
    private var allProjects: List<PersonalProject> = emptyList()

    fun loadProjects(userId: Long) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.getPersonalProject("Bearer ${GlobalData.token}", GlobalData.Rsakey, userId)
            Log.d("ProjectPersonalVM", "Bearer ${GlobalData.token}, Rsakey: ${GlobalData.Rsakey}, userId: $userId")
            if (response.code == 200) {
                val data = response.data as? List<Map<String, Any>>
                allProjects = data?.map { map ->
                    PersonalProject(
                        uuid = map["uuid"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        createdTime = map["createdTime"] as? String ?: "接不到啊哥们 ",
                        isPublic = map["isPublic"] as? Boolean ?: false,
                        id = (map["id"] as? Number)?.toLong() ?: 0L,
                        userId = (map["userId"] as? Number)?.toLong() ?: 0L,
                        power = (map["power"] as? Number)?.toInt(),
                        userRole = (map["userRole"] as? Number)?.toInt()
                    )
                } ?: emptyList()
                Log.d("PersonalProjectVM", "Mapped projects: $allProjects")
                _projects.value = allProjects
            }

        }
    }

    fun filterProjectsByName(name: String) {
        _projects.value = if (name.isEmpty()) {
            allProjects
        } else {
            allProjects.filter { it.name.contains(name, ignoreCase = true) }
        }
    }

    fun deleteProject(uuid: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.deleteProject("Bearer ${GlobalData.token}", GlobalData.Rsakey, uuid)
            if (response.code == 200) {
                allProjects = allProjects.filter { it.uuid != uuid }
                _projects.value = allProjects
                onResult(true)
            } else {
                onResult(false)
                Log.e("PersonalProjectVM", "Failed to delete project: ${response.msg}")
            }
        }
    }

    fun exitProject(projectId: String, userId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.exitProject("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId, userId)
            if (response.code == 200) {
                allProjects = allProjects.filter { it.uuid != projectId }
                _projects.value = allProjects
                onResult(true)
            } else {
                onResult(false)
                Log.e("PersonalProjectVM", "Failed to exit project: ${response.msg}")
            }
        }
    }

    suspend fun authenticate(userId: Long, projectId: String) : Boolean{
            return try {
                val response = RetrofitClient.instance.authentication("Bearer ${GlobalData.token}", GlobalData.Rsakey, userId, projectId)
                if(response.code == 200) {
                    val data = response.data as? Map<String, Any>
                    val auth = data?.let { map ->
                        UserAuthentication(
                            userId = (map["userId"] as? Number) ?.toLong() ?: 0L,
                            projectId = (map["projectId"] as? Number) ?.toLong() ?: 0L,
                            power = (map["power"] as? Number)?.toInt() ?: 0,
                            userRole = (map["userRole"] as? Number)?.toInt() ?: 0
                        )
                    }
                    Log.d("PersonalProjectVM", "Authentication result: ${auth?.userRole}")
                    auth?.userRole == 0 || auth?.userRole == 1
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
    }

    suspend fun authenticateBoss(projectId: String) : Boolean {
        return try {
            val response = RetrofitClient.instance.authenticationBossCount("Bearer ${GlobalData.token}", GlobalData.Rsakey, projectId)
            Log.d("personalProjectVM", "Bearer ${GlobalData.token}, Rsakey: ${GlobalData.Rsakey}, projectId: $projectId")
            if( response.code == 200) {
                val data = response.data
                Log.d("PersonalProjectVM", "Boss count result: $data")
                data != 1.0
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
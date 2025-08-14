package com.example.pmp.viewModel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.data.model.UserAuthentication
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PersonalProjectVM: ViewModel() {

    private val _projects = MutableLiveData<List<PersonalProject>>()
    val projects: LiveData<List<PersonalProject>> = _projects
    private var allProjects: List<PersonalProject> = emptyList()

    fun loadProjects(userId: Long) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.getPersonalProject(userId)
            if (response.code == 200) {
                val data = response.data as? List<Map<String, Any>>
                allProjects = data?.map { map ->
                    PersonalProject(
                        uuid = map["uuid"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        createTime = map["createTime"] as? String ?: "接不到啊哥们 ",
                        isPublic = map["isPublic"] as? Boolean ?: false,
                        id = (map["id"] as? Number)?.toLong() ?: 0L,
                        userId = (map["userId"] as? Number)?.toLong() ?: 0L,
                        power = (map["power"] as? Number)?.toInt(),
                        userRole = (map["userRole"] as? Number)?.toInt()
                    )
                } ?: emptyList()
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
            val response = RetrofitClient.instance.deleteProject(uuid)
            if (response.code == 200) {
                // Remove from allProjects and update LiveData
                allProjects = allProjects.filter { it.uuid != uuid }
                _projects.value = allProjects
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    suspend fun authenticate(userId: Long, projectId: String) : Boolean{
            return try {
                val response = RetrofitClient.instance.authentication(userId, projectId)
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
}
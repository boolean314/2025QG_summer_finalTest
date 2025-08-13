package com.example.pmp.viewModel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.code
import kotlin.text.get

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
                    val createTimeString = (map["created_time"] ?: map["createTime"])?.toString() ?: ""
                    Log.d("PersonalProjectVM", "createTimeString: $createTimeString") // 打印时间字符串

                    val createTime = if (createTimeString.isNotEmpty()) {
                        try {
                            // 使用适合包含毫秒部分的格式
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                            LocalDateTime.parse(createTimeString, formatter)
                        } catch (e: Exception) {
                            Log.e("PersonalProjectVM", "Error parsing date: $e")
                            LocalDateTime.now() // 默认时间
                        }
                    } else {
                        Log.e("PersonalProjectVM", "createTimeString is empty, using default time")
                        LocalDateTime.now() // 没有时间数据时使用当前时间
                    }

                    PersonalProject(
                        uuid = map["uuid"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        createTime = createTime,
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
}
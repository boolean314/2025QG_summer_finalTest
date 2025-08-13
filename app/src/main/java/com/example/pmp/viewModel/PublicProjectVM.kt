package com.example.pmp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.data.model.PublicProject
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PublicProjectVM : ViewModel() {
    private val _projects = MutableLiveData<List<PublicProject>>()
    val projects: LiveData<List<PublicProject>> = _projects
    private var allProjects: List<PublicProject> = emptyList()

    fun loadProjects(userId: Long) {
        viewModelScope.launch {
            val response = RetrofitClient.instance.getPublicProject()
            if (response.code == 200) {
                val data = response.data as? List<Map<String, Any>>
                allProjects = data?.map { map ->
                    val createTimeString = (map["created_time"] ?: map["createTime"])?.toString() ?: ""
                    Log.d("PublicProjectVM", "createTimeString: $createTimeString") // 打印时间字符串

                    val createTime = if (createTimeString.isNotEmpty()) {
                        try {
                            // 使用适合包含毫秒部分的格式
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                            LocalDateTime.parse(createTimeString, formatter)
                        } catch (e: Exception) {
                            Log.e("PublicProjectVM", "Error parsing date: $e")
                            LocalDateTime.now() // 默认时间
                        }
                    } else {
                        Log.e("PublicProjectVM", "createTimeString is empty, using default time")
                        LocalDateTime.now() // 没有时间数据时使用当前时间
                    }

                    PublicProject(
                        uuid = map["uuid"] as? String ?: "",
                        name = map["name"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        createTime = createTime,
                        isPublic = map["isPublic"] as? Boolean ?: true,
                        webhook = map["webhook"] as? String ?: "",
                        invitedCode = map["invitedCode"] as? String ?: "",
                        groupCode = map["groupCode"] as? String ?: "",
                        isDeleted = (map["isDeleted"] as? Boolean ?: "false") as Boolean
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
}
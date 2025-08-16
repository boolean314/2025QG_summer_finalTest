package com.example.pmp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.chatItem
import com.example.pmp.data.retrofit.AIRetrofitClient
import kotlinx.coroutines.launch

class AIChatVM : ViewModel() {

    val userAvatar = MutableLiveData<String>()
    val sendMsg = MutableLiveData<String>()
    val receiveMsg = MutableLiveData<String>()

    fun sendMsg(message: String, projectId: String) {
        viewModelScope.launch {
            try {
                val content = chatItem(message, projectId)
                val response = AIRetrofitClient.instance.chat(content)
                if (response.code == 200) {
                    val dataMap = response.data as Map<*, *>
                    val responseMsg = dataMap["reply"] as? String
                    receiveMsg.value = responseMsg ?: "No response from AI"
                } else {
                    receiveMsg.value = "Error"
                }
            } catch (e: Exception) {
                receiveMsg.value = "Exception: ${e.message}"
            }
        }
    }

}
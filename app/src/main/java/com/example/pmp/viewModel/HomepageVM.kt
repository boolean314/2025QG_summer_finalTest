package com.example.pmp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.code

class HomepageVM : ViewModel() {
    val userAvatar = MutableLiveData<String>()
    val username = MutableLiveData<String>()

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            try {
                val userId = GlobalData.userInfo?.id ?: return@launch
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                val response = RetrofitClient.instance.modifyAvatar(userId, body)
                if (response.code == 200) {
                    val newAvatarUrl = response.data as? String ?: ""
                    GlobalData.userInfo = GlobalData.userInfo?.copy(avatar = newAvatarUrl)
                    userAvatar.value = newAvatarUrl
                }
            } catch (e: Exception) {}
        }
    }
}
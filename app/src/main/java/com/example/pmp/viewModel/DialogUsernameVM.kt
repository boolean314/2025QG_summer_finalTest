package com.example.pmp.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.EncryptModify
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.retrofit.RetrofitClient
import com.example.pmp.util.Encryption.ModifyUsernameEncryption
import kotlinx.coroutines.launch

class DialogUsernameVM : ViewModel() {

    val newUsername = MutableLiveData<String>()
    val serverPublicKey = """-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQBfHrU7NYVB8l0kmD79ayRbS2Nmu0gOKIg177flG/MiZd5TIYuH+eOINrFFgu6K1jmTqeUDw5Lm2SPofC1fV++V6yhJu8Vveaa0WhFElSrp5F4vsZ34HB7kpZmH6Vp/u9tdohDrXe+cVdO74ILxsw9CLpEpFrFHmgThVSKtNfwCExZeOT5lN6UKgsxp+HIFbhKWF9NMpmeYw5ie10YevN9Fq9x11aeg+ZgKct1GzF9RfOcX0h6Mz4xu45q5bWRQS+djvprBS5tvYOCVZj9KEanltbFFq71PmiQLdkH7imCFtwHPZzK5TAYeknH+raSjlGDMsijs+I8tR8XpuQcXtwIDAQAB
        -----END PUBLIC KEY-----""".trimIndent()

    fun modify(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (encryptedData, encryptedKey) = ModifyUsernameEncryption.encryptWithServerKey(
                    GlobalData.userInfo?.id ?: 0,
                    newUsername.value ?: "",
                    serverPublicKey
                )
                val user = EncryptModify(encryptedData, encryptedKey)
                val response = RetrofitClient.instance.modifyU("Bearer ${GlobalData.token}", GlobalData.Rsakey, user)
                    if (response.code == 200) {
                        Log.d("DialogUsernameVM", "修改后的信息：username = ${newUsername.value}")
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        GlobalData.userInfo = GlobalData.userInfo?.copy(username = newUsername.value?: "")
                        onSuccess()
                    } else {
                        onError(response.msg)
                    }
            } catch (e: Exception) {
                onError("网络开小差了，请重试")
            }
        }

    }
}
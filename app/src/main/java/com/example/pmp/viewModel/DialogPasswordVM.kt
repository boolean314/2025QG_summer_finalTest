package com.example.pmp.viewModel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmp.data.model.EncryptModify
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.retrofit.RetrofitClient
import com.example.pmp.util.Encryption.ModifyPasswordEncryption
import com.example.pmp.util.Encryption.ModifyUsernameEncryption
import com.example.pmp.util.Encryption.VerifyCodeEncryption
import kotlinx.coroutines.launch

class DialogPasswordVM : ViewModel() {
    val email = MutableLiveData<String>()
    val verifyCode = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val newPasswordAgain = MutableLiveData<String>()
    private var canSendCode = true
    val serverPublicKey = """-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQBfHrU7NYVB8l0kmD79ayRbS2Nmu0gOKIg177flG/MiZd5TIYuH+eOINrFFgu6K1jmTqeUDw5Lm2SPofC1fV++V6yhJu8Vveaa0WhFElSrp5F4vsZ34HB7kpZmH6Vp/u9tdohDrXe+cVdO74ILxsw9CLpEpFrFHmgThVSKtNfwCExZeOT5lN6UKgsxp+HIFbhKWF9NMpmeYw5ie10YevN9Fq9x11aeg+ZgKct1GzF9RfOcX0h6Mz4xu45q5bWRQS+djvprBS5tvYOCVZj9KEanltbFFq71PmiQLdkH7imCFtwHPZzK5TAYeknH+raSjlGDMsijs+I8tR8XpuQcXtwIDAQAB
        -----END PUBLIC KEY-----""".trimIndent()

    fun verifyCode(context: Context, button: Button) {
        if (!canSendCode) {
            Toast.makeText(context, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show()
            return
        }
        canSendCode = false
        button.isEnabled = false
        viewModelScope.launch {
            try {
                val (encryptedData, encryptedKey) = VerifyCodeEncryption.encryptWithServerKey(
                    email.value ?: "",
                    serverPublicKey
                )
                RetrofitClient.instance.getVerifyCode(encryptedData, encryptedKey)
            } catch (e: Exception) {}
        }
//        Toast.makeText(context, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            canSendCode = true
            button.isEnabled = isEmailValid(email.value ?: "")
        }, 60000)
    }

    fun modify(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (encryptedData, encryptedKey) = ModifyPasswordEncryption.encryptWithServerKey(
                    email.value?: "",
                    verifyCode.value?: "",
                    newPassword.value ?: "",
                    serverPublicKey
                )
                val user = EncryptModify(encryptedData, encryptedKey)
                val response = RetrofitClient.instance.modifyP(user)
                if (response.code == 200) {
                    Log.d("DialogPasswordVM", "修改后的信息：password = ${newPassword.value}")
                    Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                    GlobalData.userInfo = GlobalData.userInfo?.copy(password = newPassword.value?: "")
                    onSuccess()
                } else {
                    onError(response.msg)
                }
            } catch (e: Exception) {
                onError("网络开小差了，请重试")
            }
        }
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
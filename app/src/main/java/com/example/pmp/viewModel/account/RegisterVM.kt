package com.example.pmp.viewModel.account

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd.CircularProgressButton
import com.example.pmp.data.model.EncryptRegister
import com.example.pmp.data.retrofit.RetrofitClient
import com.example.pmp.ui.LR.Login
import com.example.pmp.util.Encryption.RegisterEncryption
import com.example.pmp.util.Encryption.VerifyCodeEncryption
import kotlinx.coroutines.launch

class RegisterVM : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val verifyCode = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
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
        Toast.makeText(context, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            canSendCode = true
            button.isEnabled = isEmailValid(email.value ?: "")
        }, 60000)
    }

    fun register(context: Context, button: CircularProgressButton) {
        button.progress = CircularProgressButton.INDETERMINATE_STATE_PROGRESS
        viewModelScope.launch {
            try {
                val (encryptedData, encryptedKey) = RegisterEncryption.encryptWithServerKey(
                    email.value ?: "",
                    password.value ?: "",
                    verifyCode.value ?: "",
                    phone.value ?: "",
                    serverPublicKey
                )
                val user = EncryptRegister(encryptedData, encryptedKey)
                val response = RetrofitClient.instance.register(user)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (response.code == 201) {
                        button.progress = CircularProgressButton.SUCCESS_STATE_PROGRESS
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            context.startActivity(Intent(context, Login::class.java))
                        },1000)
                    } else {
                        button.progress = CircularProgressButton.ERROR_STATE_PROGRESS
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            button.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                        }, 2000)
                    }
                },2000)
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).postDelayed({
                    button.progress = CircularProgressButton.ERROR_STATE_PROGRESS
                    Toast.makeText(context, "网络开小差了，请重试", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        button.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                    },2000)
                },2000)
            }
        }
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
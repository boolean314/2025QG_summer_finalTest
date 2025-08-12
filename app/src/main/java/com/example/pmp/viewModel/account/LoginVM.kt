package com.example.pmp.viewModel.account

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd.CircularProgressButton
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.ui.Container
import kotlinx.coroutines.launch

import com.example.pmp.data.retrofit.RetrofitClient
import com.example.pmp.util.Encryption.LoginEncryption

class LoginVM : ViewModel() {
    val account = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val serverPublicKey = """-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQBfHrU7NYVB8l0kmD79ayRbS2Nmu0gOKIg177flG/MiZd5TIYuH+eOINrFFgu6K1jmTqeUDw5Lm2SPofC1fV++V6yhJu8Vveaa0WhFElSrp5F4vsZ34HB7kpZmH6Vp/u9tdohDrXe+cVdO74ILxsw9CLpEpFrFHmgThVSKtNfwCExZeOT5lN6UKgsxp+HIFbhKWF9NMpmeYw5ie10YevN9Fq9x11aeg+ZgKct1GzF9RfOcX0h6Mz4xu45q5bWRQS+djvprBS5tvYOCVZj9KEanltbFFq71PmiQLdkH7imCFtwHPZzK5TAYeknH+raSjlGDMsijs+I8tR8XpuQcXtwIDAQAB
        -----END PUBLIC KEY-----""".trimIndent()
    fun login(context: Context, button: CircularProgressButton) {
        button.progress = CircularProgressButton.INDETERMINATE_STATE_PROGRESS
        viewModelScope.launch {
            try {
                val (encryptedData, encryptedKey) = LoginEncryption.encryptWithServerKey(
                    account.value ?: "",
                    password.value ?: "",
                    serverPublicKey
                )
                val user = EncryptLogin(encryptedData, encryptedKey)
                val response = RetrofitClient.instance.login(user)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (response.code == 200) {
                        button.progress = CircularProgressButton.SUCCESS_STATE_PROGRESS
                        Handler(Looper.getMainLooper()).postDelayed({
                            context.startActivity(Intent(context, Container::class.java))
                        },1000)
                    } else {
                        button.progress = CircularProgressButton.ERROR_STATE_PROGRESS
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            button.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                        }, 2000)
                    }
                },2000)
                val userMsg = response.data
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).postDelayed({
                    button.progress = CircularProgressButton.ERROR_STATE_PROGRESS
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        button.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                    },2000)
                },2000)
            }
        }
    }
}
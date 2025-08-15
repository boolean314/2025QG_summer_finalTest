package com.example.pmp.viewModel.account

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd.CircularProgressButton
import com.example.pmp.data.model.EncryptLogin
import com.example.pmp.data.model.EncryptedToken
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.UserInfo
import com.example.pmp.ui.Container
import kotlinx.coroutines.launch
import com.example.pmp.data.retrofit.RetrofitClient
import com.example.pmp.util.Decryption.Decryption
import com.example.pmp.util.Encryption.LoginEncryption
import com.example.pmp.util.Encryption.TokenEncryption

class LoginVM : ViewModel() {
    val account = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val serverPublicKey = """-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQBfHrU7NYVB8l0kmD79ayRbS2Nmu0gOKIg177flG/MiZd5TIYuH+eOINrFFgu6K1jmTqeUDw5Lm2SPofC1fV++V6yhJu8Vveaa0WhFElSrp5F4vsZ34HB7kpZmH6Vp/u9tdohDrXe+cVdO74ILxsw9CLpEpFrFHmgThVSKtNfwCExZeOT5lN6UKgsxp+HIFbhKWF9NMpmeYw5ie10YevN9Fq9x11aeg+ZgKct1GzF9RfOcX0h6Mz4xu45q5bWRQS+djvprBS5tvYOCVZj9KEanltbFFq71PmiQLdkH7imCFtwHPZzK5TAYeknH+raSjlGDMsijs+I8tR8XpuQcXtwIDAQAB
        -----END PUBLIC KEY-----""".trimIndent()
    val serverPrivateKey = """-----BEGIN PRIVATE KEY-----
        MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC1AZj7Ik201dBfzZ9eP3rJCsdt9Hy7RP9KR+xTWnI+VGrW+fDUAzYyFzegXemaXpgePTchdbeioow+JkUPkWQWOmOlmgg+aXMymqgvkix1e3MrepRJkRNVXJJ5KvSJt7OPR5K5B8QTyLXEP8sOLaVpfTJxOmrbe5EwLf1iF63JmL+JoM3vxFZX/kroirb9fYCsWfaZG8IIil5SXlgS7UPd5mpy+pXn0QrhwP2Gw23nG8pX+AwTuL65dJC8DSZMfvFdWZeTQnw6AVS8StMmvXazMNWgOzA65kCJlQB62T69/CYttAjGGXzbWZ6Koe0/TOtpADIdLDOPIe++lxm6Rrn9AgMBAAECggEASPwDbOPIlG2Qb0jQhWawQkc74cyuzK4GCDQXCQcTwKk2SUePwVUoMatl7R5g9rNEwBCr3ayDJqtHRDoXJ69WvZW+n0QMJepMHm/49/GHRrnH1xS+nSlHs+g3UW8uGie92byg30XP3LBWBnM4k5d5Np9aSwiklKpvAQ/SNw7YLsxgG3tjmMgzbzQnNtTdW81BV+A4KIaYVUZnoSEVSzHN3T7WZgG9TLiCm7AowQ8qFTK0Sxu0kO3JGc6G3GkQmR77J4YDIv8O+Da+ITmyVEwxtzuIKNa/VDCtV3Anxit+Hk0xBNsT9Vvdv3upMyjOggjXnWbQYUXN4zbv1IoWlwqOiwKBgQDlzIUZHQPY5t1BWOOXr1w7KRVfxtqm9fKy1x4v+T7f1SPOmFpmAg3Qfv6c1dpON8d6NJS01kuymFW/iPVdvjpoGWqYBSEn0mbz9iBU9quNaU4WhqmlU8OdfqCch0jOK/l+3WCl4BshShpis2mvXPa8HtygkRqQfMVGML2nGttb+wKBgQDJpOTiJSRlcRgj27qXsQjXwrJsEAoZ0cA/UaUlWed/qHBsLE9yk+Y3bxlFc8Sf0wLJejaaCOQ4IS6e81bl7AOT/VBqU77zzBS1uZ4L1dMlJipELgtQcsv06CblDhnRJLAAJ0/xtX6HUqu2v8pGqaLyqsESZXn8TdwxyBKfbnioZwKBgQCIgQ7nNhcc9zajJLw9VIvDEMqDlEo6N4sttR9XfAVfTOryRAoe4kV2fpmcbGQ7ZmL2MtnK+ikJM/hryF2IjAGB6Ocq2pExaIiDjsbx8X1CiTU7qE6JyNJAcgHSOYKEBhc0xygsII29HpnB27WB2AUxBlwkfU18WsGMylM+OnPnlQKBgQC7MtQyhlzVuDq6/4Co1vfopp3R6MoX0jxyDDAPDvn177//DNvs+RVfHUsOyT0fS1xpA4axVdPZsCSB+FMSPRvNRfxj2b+Kwknvs5TgU/AjqtzOUxi55PkoMmX5fC/HlBG48sYrFV2T79HuZPs6wr2+H3wCwiaPbxEfPijbzklBvQKBgBFCAippRBOSX6gol9VJSwzB61Ak9U2vYKucWia4GrEtS/faEUmNKj220qksTEjiACnbTrWojZFKEWx30s+mrkwXdXmNbuq2so5fEvjGQ8rKXCcJNp5/pInPMvhCvw4tUuJ9lEH8EXkDFFRlTVoUnlWofHdTcQreOs/tkHlfrC5M
        -----END PRIVATE KEY-----"""

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
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            context.startActivity(Intent(context, Container::class.java))
                        },1000)
                        val dataMap = response.data as Map<*, *>
                        val encryptedData = dataMap["encryptedData"]
                        val encryptedKey = dataMap["encryptedKey"]
                        if (encryptedData !is String || encryptedKey !is String) {
                            Log.e("LoginVM", "encryptedData or encryptedKey is not a String! Actual types: ${encryptedData?.javaClass}, ${encryptedKey?.javaClass}")
                            return@postDelayed
                        }
                        val decryptedJson = Decryption.decrypt(encryptedData,encryptedKey,serverPrivateKey)
                        val decryptedObj = org.json.JSONObject(decryptedJson)
                        val userObj = decryptedObj.getJSONObject("user")
                        val id = userObj.optLong("id")
                        val username = userObj.optString("username")
                        val avatar = userObj.optString("avatar")
                        val createdTime = userObj.optString("createdTime")
                        val phone = userObj.optString("phone")
                        val token = decryptedObj.optString("token")
                        val userInfo = UserInfo(
                            id = id,
                            username = username,
                            password = password.value ?: "",
                            avatar = avatar,
                            createdTime = createdTime,
                            phone = phone,
                            token = token
                        )
                        GlobalData.userInfo = userInfo
                        Log.d("LoginVM", "userInfo: ${GlobalData.userInfo}")
                        val (encryptedDataT, encryptedKeyT) = TokenEncryption.encryptWithServerKey(
                            GlobalData.userInfo?.token,
                            serverPublicKey
                        )
                        val tokenData = EncryptedToken(encryptedDataT, encryptedKeyT)
                        GlobalData.token = tokenData
                        Log.d("LoginVM", "Token encryptedData: ${GlobalData.token?.encryptedData}")
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
                    Toast.makeText(context, "网络开小差了，请重试", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        button.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                    },2000)
                },2000)
            }
        }
    }
}
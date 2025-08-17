package com.example.pmp.viewModel

import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.ListMails
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class MailsWebSocketListener(
    private val mailsVM: MailsVM
) : WebSocketListener() {
    override fun onMessage(webSocket: WebSocket, text: String) {
        val json = JSONObject(text)
        val dataObj = json.optJSONObject("data")
        val dataArray = dataObj?.optJSONArray("data")
        if (dataArray != null && dataArray.length() > 0) {
            val item = dataArray.optJSONObject(0)
            val receiverId = item?.optInt("receiverId")
            val userId = GlobalData.userInfo?.id?.toInt()
            val mail = ListMails(
                errorId = item?.optInt("errorId"),
                errorMessage = item?.optString("errorMessage") ?: "",
                errorType = item?.optString("errorType") ?: "",
                id = item?.optInt("id"),
                platform = item?.optString("platform") ?: "",
                projectId = item?.optString("projectId") ?: "",
                projectName = item?.optString("projectName") ?: "",
                responsibleName = item?.optString("responsibleName") ?: "",
                senderAvatar = item?.optString("senderAvatar") ?: "",
                senderName = item?.optString("senderName") ?: "",
                receiverId = receiverId,
                isHandled = false
            )
            if (mail.receiverId != null && mail.receiverId == userId?.toInt()) {
                mailsVM.addMails(mail)
            }
        }
    }
}
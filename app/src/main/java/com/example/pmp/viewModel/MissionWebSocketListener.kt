package com.example.pmp.viewModel

import com.example.pmp.data.model.ListMissions
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import kotlin.compareTo

class MissionWebSocketListener(
    private val missionsVM: MissionsVM
) : WebSocketListener() {
    override fun onMessage(webSocket: WebSocket, text: String) {
        val json = JSONObject(text)
        val dataObj = json.optJSONObject("data")
        val dataArray = dataObj?.optJSONArray("data")
        if (dataArray != null && dataArray.length() > 0) {
            val item = dataArray.optJSONObject(0)
            val responsibleId = item?.optInt("responsibleId")
            val userId = com.example.pmp.data.model.GlobalData.userInfo?.id?.toInt()
            android.util.Log.d("WebSocket", "responsibleId: $responsibleId, userId: $userId")
            val mission = ListMissions(
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
                responsibleId = responsibleId,
                isHandled = false
            )
            if (mission.responsibleId != null && mission.responsibleId == userId?.toInt()) {
                missionsVM.addMission(mission)
            }
        }
    }
}
package com.example.pmp.data.model

import android.content.Context
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketModel: WebSocketContract {

    private var webSocket: WebSocket?= null
    private val client by lazy {
        OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
    }

     fun connect(listener: WebSocketListener, context: Context) {
        val request = okhttp3.Request.Builder()
            .url("ws://47.113.224.195:30420/ws")
            .build()
        webSocket = client.newWebSocket(request, listener)
        Toast.makeText(context, "WebSocket连接成功", Toast.LENGTH_LONG).show()
    }

    override fun connect(listener: WebSocketListener) {}

    override fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        client.dispatcher.executorService.shutdown()
        webSocket = null
    }
}
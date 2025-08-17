package com.example.pmp.data.model

import okhttp3.WebSocketListener

interface WebSocketContract {
    fun connect (listener: WebSocketListener)
    fun disconnect()

}
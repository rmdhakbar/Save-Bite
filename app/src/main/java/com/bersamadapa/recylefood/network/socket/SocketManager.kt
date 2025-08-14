package com.bersamadapa.recylefood.network.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URI

class SocketManager(private var userId: String? = null) {

    private var socket: Socket? = null

    init {
        try {
            val socketUrl = "https://enormous-mint-tomcat.ngrok-free.app" // Replace with your Socket.IO server URL
            val uri = URI.create(socketUrl)
            socket = IO.socket(uri)
        } catch (e: Exception) {
            Log.e("SocketManager", "Error initializing socket", e)
        }
    }

    fun connect() {
        socket?.connect()
        userId?.let {
            // Emit the userId to associate the socket with the user
            socket?.emit("register", it) // Sends userId to the server
        }
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun onEvent(event: String, onMessageReceived: (String) -> Unit) {
        socket?.on(event) { args ->
            if (args.isNotEmpty()) {
                onMessageReceived(args[0] as String)
            }
        }
    }

    fun emitEvent(event: String, data: String) {
        socket?.emit(event, data)
    }


    fun listenForNotifications(onNotificationReceived: (String, String) -> Unit) {
        socket?.on("notification") { args ->
            if (args.isNotEmpty()) {
                // Assuming the notification is a JSONObject
                val notification = args[0] as JSONObject
                val title = notification.optString("title", "No Title") // Use optString to safely access the value
                val message = notification.optString("message", "No Message")

                // Pass the title and message to the callback
                onNotificationReceived(title, message)
            }
        }
    }



    // Update userId if user logs in
    fun updateUserId(newUserId: String) {
        userId = newUserId
        socket?.emit("register", newUserId) // Send updated userId to the server
    }
}

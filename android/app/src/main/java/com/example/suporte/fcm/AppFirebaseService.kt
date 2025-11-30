package com.example.suporte.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.suporte.di.ApiService

class AppFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "Atualização"
        val body = message.notification?.body ?: message.data["message"] ?: "Você tem uma notificação"
        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Novo token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://braylen-kaolinic-tabularly.ngrok-free.dev//suporte_tecnico//server/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.salvarFcmToken(userId, token)
                    if (response["status"] == "ok") {
                        Log.d("FCM", "Token enviado com sucesso para o servidor")
                    } else {
                        Log.e("FCM", "Erro ao enviar token para o servidor")
                    }
                } catch (e: Exception) {
                    Log.e("FCM", "Erro ao enviar token: ${e.message}")
                }
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "suporte_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Suporte", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(1, notification)
    }
}

package com.capstone.smart.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.capstone.smart.MainActivity
import com.capstone.smart.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Kirim token ini ke Backend Laravel (simpan di tabel user)
        // agar backend tahu perangkat mana yang harus dikirimi notifikasi
        println("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Cek apakah pesan berisi data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Notifikasi Baru"
            val body = remoteMessage.data["body"] ?: "Anda memiliki pesan baru."
            sendNotification(title, body)
        }

        // Cek apakah pesan berisi notification payload (FCM default).
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "smaRT", it.body ?: "")
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "smart_fcm_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Ganti dengan icon notifikasi Anda (ic_stat_name)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Sejak Android Oreo (API 26), notifikasi memerlukan NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi smaRT",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}

package com.capstone.smart.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.capstone.smart.MainActivity
import com.capstone.smart.R
import com.capstone.smart.data.model.FcmTokenRequest
import com.capstone.smart.data.remote.ApiClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val PREFS_NAME = "fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"

        /**
         * Ambil FCM token yang tersimpan di SharedPreferences.
         * Dipakai untuk mengirim ulang token ke backend setelah login.
         */
        fun getSavedToken(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_FCM_TOKEN, null)
        }

        /**
         * Dapatkan device ID unik berbasis ANDROID_ID.
         */
        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token baru: $token")

        // Simpan ke SharedPreferences
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FCM_TOKEN, token)
            .apply()

        // Kirim ke backend (hanya jika sudah login / ada auth token)
        sendTokenToBackend(token)
    }

    /**
     * Kirim FCM token ke backend Laravel.
     * Hanya berjalan jika user sudah login (ApiClient.authToken != null).
     */
    private fun sendTokenToBackend(token: String) {
        val authToken = ApiClient.authToken ?: return

        val deviceId = getDeviceId(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.sendFcmToken(
                    FcmTokenRequest(token, deviceId)
                )
                if (response.isSuccessful) {
                    Log.d(TAG, "FCM token berhasil dikirim ke backend")
                } else {
                    Log.e(TAG, "Gagal kirim FCM token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error kirim FCM token", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Pesan diterima dari: ${remoteMessage.from}")

        // Cek apakah pesan berisi data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Notifikasi Baru"
            val body = remoteMessage.data["body"] ?: "Anda memiliki pesan baru."
            val type = remoteMessage.data["type"] // e.g. "broadcast", "surat", "panic"
            sendNotification(title, body, type)
        }

        // Cek apakah pesan berisi notification payload (FCM default).
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "smaRT", it.body ?: "")
        }
    }

    private fun sendNotification(title: String, messageBody: String, type: String? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Tambahkan data type agar app bisa navigate ke screen yang benar
            type?.let { putExtra("notification_type", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "smart_fcm_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Sejak Android Oreo (API 26), notifikasi memerlukan NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi smaRT",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi dari aplikasi smaRT"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Gunakan unique ID agar notifikasi tidak saling timpa
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

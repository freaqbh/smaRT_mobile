package com.capstone.smart.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.smart.data.model.*
import com.capstone.smart.data.repository.SmaRTRepository
import com.capstone.smart.service.MyFirebaseMessagingService
import kotlinx.coroutines.launch
import java.io.File

class SmaRTViewModel : ViewModel() {

    // ═══════════ AUTH STATE ═══════════
    var currentUser by mutableStateOf<User?>(null)
        private set

    // ═══════════ BROADCAST STATE ═══════════
    var broadcasts by mutableStateOf<List<Broadcast>>(emptyList())
        private set
    var broadcastLoading by mutableStateOf(false)
        private set

    // ═══════════ SURAT STATE ═══════════
    var suratList by mutableStateOf<List<Surat>>(emptyList())
        private set
    var suratLoading by mutableStateOf(false)
        private set
    var suratRiwayat by mutableStateOf<List<Surat>>(emptyList())
        private set
    var suratRiwayatLoading by mutableStateOf(false)
        private set
    var suratSubmitLoading by mutableStateOf(false)
        private set
    var suratSubmitResult by mutableStateOf<String?>(null)
        private set

    // ═══════════ KAS STATE ═══════════
    var kasMonitor by mutableStateOf<KasMonitorResponse?>(null)
        private set
    var kasHistory by mutableStateOf<List<KasTransaction>>(emptyList())
        private set
    var kasLoading by mutableStateOf(false)
        private set
    var kasVerifyLoading by mutableStateOf(false)
        private set
    var kasVerifyResult by mutableStateOf<KasMonitorResponse?>(null)
        private set

    // ═══════════ PANIC STATE ═══════════
    var panicLoading by mutableStateOf(false)
        private set
    var panicResult by mutableStateOf<String?>(null)
        private set
    var panicError by mutableStateOf<String?>(null)
        private set

    // ═══════════ AGENDA STATE ═══════════
    var agendaList by mutableStateOf<List<Broadcast>>(emptyList())
        private set
    var agendaLoading by mutableStateOf(false)
        private set

    // ═══════════ AUTH METHODS ═══════════

    fun login(
        nik: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = SmaRTRepository.login(nik, password)
            result.onSuccess { response ->
                currentUser = response.user
                onSuccess()
            }
            result.onFailure { e ->
                onError(e.message ?: "Login gagal.")
            }
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Hapus FCM token dari backend sebelum logout
            try {
                val fcmToken = MyFirebaseMessagingService.getSavedToken(context)
                val deviceId = MyFirebaseMessagingService.getDeviceId(context)
                if (fcmToken != null) {
                    SmaRTRepository.deleteFcmToken(fcmToken, deviceId)
                }
            } catch (e: Exception) {
                Log.e("SmaRTViewModel", "Gagal hapus FCM token", e)
            }

            SmaRTRepository.logout()
            currentUser = null
            onComplete()
        }
    }

    // ═══════════ FCM METHODS ═══════════

    /**
     * Kirim FCM token ke backend setelah login berhasil.
     */
    fun sendFcmToken(context: Context) {
        val fcmToken = MyFirebaseMessagingService.getSavedToken(context)
        if (fcmToken != null) {
            sendTokenToRepo(context, fcmToken)
        } else {
            // Coba ambil secara asinkron dari Firebase
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("SmaRTViewModel", "Berhasil fetch token baru dari Firebase: $token")
                    // Simpan ke prefs
                    context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("fcm_token", token)
                        .apply()
                    sendTokenToRepo(context, token)
                } else {
                    Log.w("SmaRTViewModel", "FCM token belum tersedia, gagal fetch", task.exception)
                }
            }
        }
    }

    private fun sendTokenToRepo(context: Context, token: String) {
        viewModelScope.launch {
            try {
                val deviceId = MyFirebaseMessagingService.getDeviceId(context)
                val result = SmaRTRepository.sendFcmToken(token, deviceId)
                result.onSuccess {
                    Log.d("SmaRTViewModel", "FCM token terkirim ke backend")
                }
                result.onFailure {
                    Log.e("SmaRTViewModel", "Gagal kirim FCM token: ${it.message}")
                }
            } catch (e: Exception) {
                Log.e("SmaRTViewModel", "Error kirim FCM token", e)
            }
        }
    }

    // ═══════════ BROADCAST METHODS ═══════════

    fun loadBroadcasts() {
        viewModelScope.launch {
            broadcastLoading = true
            val result = SmaRTRepository.getBroadcasts(5)
            result.onSuccess { broadcasts = it }
            broadcastLoading = false
        }
    }

    // ═══════════ SURAT METHODS ═══════════

    fun loadSuratList() {
        viewModelScope.launch {
            suratLoading = true
            val result = SmaRTRepository.getSuratList()
            result.onSuccess { suratList = it }
            suratLoading = false
        }
    }

    fun loadSuratRiwayat() {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            suratRiwayatLoading = true
            val result = SmaRTRepository.getSuratRiwayat(userId)
            result.onSuccess { suratRiwayat = it }
            suratRiwayatLoading = false
        }
    }

    fun ajukanSurat(namaSurat: String, deskripsi: String, dokumenFile: File? = null) {
        viewModelScope.launch {
            suratSubmitLoading = true
            suratSubmitResult = null
            val result = SmaRTRepository.ajukanSurat(namaSurat, deskripsi, dokumenFile)
            result.onSuccess {
                suratSubmitResult = "Surat berhasil diajukan!"
                loadSuratRiwayat() // Refresh daftar riwayat
            }
            result.onFailure {
                suratSubmitResult = it.message
            }
            suratSubmitLoading = false
        }
    }

    fun clearSuratSubmitResult() {
        suratSubmitResult = null
    }

    // ═══════════ KAS METHODS ═══════════

    fun loadKeuangan() {
        viewModelScope.launch {
            kasLoading = true
            // Load monitor & history in parallel
            val monitorResult = SmaRTRepository.getKasMonitor()
            monitorResult.onSuccess { kasMonitor = it }

            val historyResult = SmaRTRepository.getKasHistory()
            historyResult.onSuccess { kasHistory = it }
            kasLoading = false
        }
    }

    fun verifyBlockchain() {
        viewModelScope.launch {
            kasVerifyLoading = true
            kasVerifyResult = null
            val result = SmaRTRepository.getKasMonitor()
            result.onSuccess { kasVerifyResult = it }
            result.onFailure { kasVerifyResult = null }
            kasVerifyLoading = false
        }
    }

    fun clearVerifyResult() {
        kasVerifyResult = null
    }

    // ═══════════ PANIC METHODS ═══════════

    fun triggerPanic(latitude: String, longitude: String) {
        viewModelScope.launch {
            panicLoading = true
            panicResult = null
            panicError = null
            val result = SmaRTRepository.triggerPanic(latitude, longitude)
            result.onSuccess {
                panicResult = "Sinyal darurat berhasil dikirim!"
            }
            result.onFailure {
                panicError = it.message ?: "Gagal mengirim sinyal darurat."
            }
            panicLoading = false
        }
    }

    fun clearPanicResult() {
        panicResult = null
        panicError = null
    }

    // ═══════════ AGENDA METHODS ═══════════

    fun loadAgenda() {
        viewModelScope.launch {
            agendaLoading = true
            // Use broadcasts with KEGIATAN category as agenda items
            val result = SmaRTRepository.getBroadcasts(20)
            result.onSuccess { allBroadcasts ->
                agendaList = allBroadcasts.filter { it.kategori == "KEGIATAN" }
            }
            agendaLoading = false
        }
    }
}

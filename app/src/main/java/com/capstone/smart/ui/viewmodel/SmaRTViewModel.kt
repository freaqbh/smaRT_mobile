package com.capstone.smart.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.smart.data.model.*
import com.capstone.smart.data.repository.SmaRTRepository
import kotlinx.coroutines.launch

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

    // ═══════════ PANIC STATE ═══════════
    var panicLoading by mutableStateOf(false)
        private set
    var panicResult by mutableStateOf<String?>(null)
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

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            SmaRTRepository.logout()
            currentUser = null
            onComplete()
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

    fun ajukanSurat(namaSurat: String, deskripsi: String) {
        viewModelScope.launch {
            suratSubmitLoading = true
            suratSubmitResult = null
            val result = SmaRTRepository.ajukanSurat(namaSurat, deskripsi)
            result.onSuccess {
                suratSubmitResult = "Surat berhasil diajukan!"
                loadSuratList() // Refresh daftar
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

    // ═══════════ PANIC METHODS ═══════════

    fun triggerPanic(latitude: String, longitude: String) {
        viewModelScope.launch {
            panicLoading = true
            panicResult = null
            val result = SmaRTRepository.triggerPanic(latitude, longitude)
            result.onSuccess {
                panicResult = "Sinyal darurat berhasil dikirim!"
            }
            result.onFailure {
                panicResult = it.message
            }
            panicLoading = false
        }
    }

    fun clearPanicResult() {
        panicResult = null
    }
}

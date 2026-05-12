package com.capstone.smart.data.repository

import com.capstone.smart.data.model.*
import com.capstone.smart.data.remote.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Repository untuk memanggil semua API smaRT.
 * Setiap method mengembalikan Result<T> agar ViewModel bisa handle sukses/gagal.
 */
object SmaRTRepository {

    private val api get() = ApiClient.instance

    // ═══════════ AUTH ═══════════

    suspend fun login(nik: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(nik, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                ApiClient.authToken = body.token
                Result.success(body)
            } else {
                Result.failure(Exception("NIK atau password salah."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    suspend fun logout(): Result<String> {
        return try {
            val response = api.logout()
            ApiClient.authToken = null
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Berhasil logout.")
            } else {
                Result.failure(Exception("Gagal logout."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    // ═══════════ BROADCAST ═══════════

    suspend fun getBroadcasts(limit: Int = 10): Result<List<Broadcast>> {
        return try {
            val response = api.getBroadcasts(limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat broadcast."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    // ═══════════ SURAT ═══════════

    suspend fun getSuratList(): Result<List<Surat>> {
        return try {
            val response = api.getSuratList()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat daftar surat."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    suspend fun ajukanSurat(namaSurat: String, deskripsi: String): Result<Surat> {
        return try {
            val namaBody = namaSurat.toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.ajukanSurat(namaBody, deskripsiBody)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengajukan surat."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    // ═══════════ PANIC ═══════════

    suspend fun triggerPanic(lat: String, lng: String): Result<PanicLog> {
        return try {
            val response = api.triggerPanic(PanicRequest(lat, lng))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengirim sinyal darurat."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    // ═══════════ KAS / KEUANGAN ═══════════

    suspend fun getKasHistory(): Result<List<KasTransaction>> {
        return try {
            val response = api.getKasHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat riwayat kas."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    suspend fun getKasMonitor(): Result<KasMonitorResponse> {
        return try {
            val response = api.getKasMonitor()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat data keuangan."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }
}

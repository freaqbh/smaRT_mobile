package com.capstone.smart.data.repository

import com.capstone.smart.data.model.*
import com.capstone.smart.data.remote.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Repository untuk memanggil semua API smaRT.
 * Setiap method mengembalikan Result<T> agar ViewModel bisa handle sukses/gagal.
 */
object SmaRTRepository {

    private val api get() = ApiClient.instance

    // ═══════════ FCM TOKEN ═══════════

    suspend fun sendFcmToken(token: String, deviceId: String): Result<String> {
        return try {
            val response = api.sendFcmToken(FcmTokenRequest(token, deviceId))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Token tersimpan.")
            } else {
                Result.failure(Exception("Gagal menyimpan FCM token."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    suspend fun deleteFcmToken(token: String, deviceId: String): Result<String> {
        return try {
            val response = api.deleteFcmToken(FcmTokenRequest(token, deviceId))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Token dihapus.")
            } else {
                Result.failure(Exception("Gagal menghapus FCM token."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

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

    suspend fun getSuratRiwayat(userId: String): Result<List<Surat>> {
        return try {
            val response = api.getSuratRiwayat(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat riwayat surat."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }

    suspend fun ajukanSurat(
        namaSurat: String,
        deskripsi: String,
        dokumenFile: File? = null
    ): Result<Surat> {
        return try {
            val namaBody = namaSurat.toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())

            val filePart = dokumenFile?.let { file ->
                val mimeType = when {
                    file.name.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                    file.name.endsWith(".jpg", ignoreCase = true) ||
                            file.name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                    file.name.endsWith(".png", ignoreCase = true) -> "image/png"
                    else -> "application/octet-stream"
                }
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("dokumen_pendukung", file.name, requestBody)
            }

            val response = api.ajukanSurat(namaBody, deskripsiBody, filePart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Gagal mengajukan surat."
                } catch (_: Exception) {
                    "Gagal mengajukan surat (${response.code()})."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server: ${e.localizedMessage}"))
        }
    }

    // ═══════════ PANIC ═══════════

    suspend fun triggerPanic(lat: String, lng: String): Result<PanicLog> {
        return try {
            val response = api.triggerPanic(PanicRequest(lat, lng))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Gagal mengirim sinyal darurat."
                } catch (_: Exception) {
                    "Gagal mengirim sinyal darurat (${response.code()})."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server: ${e.localizedMessage}"))
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

    // ═══════════ LAPORAN WARGA ═══════════

    suspend fun kirimLaporan(
        kategoriMasalah: String,
        deskripsi: String,
        lokasi: String,
        fotoFile: File? = null
    ): Result<LaporanWarga> {
        return try {
            val kategoriBody = kategoriMasalah.toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
            val lokasiBody = lokasi.toRequestBody("text/plain".toMediaTypeOrNull())

            val fotoPart = fotoFile?.let { file ->
                val mimeType = when {
                    file.name.endsWith(".jpg", ignoreCase = true) ||
                            file.name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                    file.name.endsWith(".png", ignoreCase = true) -> "image/png"
                    else -> "image/jpeg"
                }
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto", file.name, requestBody)
            }

            val response = api.kirimLaporan(kategoriBody, deskripsiBody, lokasiBody, fotoPart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Gagal mengirim laporan."
                } catch (_: Exception) {
                    "Gagal mengirim laporan (${response.code()})."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server: ${e.localizedMessage}"))
        }
    }

    suspend fun getLaporanRiwayat(userId: String): Result<List<LaporanWarga>> {
        return try {
            val response = api.getLaporanRiwayat(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat riwayat laporan."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak bisa terhubung ke server."))
        }
    }
}

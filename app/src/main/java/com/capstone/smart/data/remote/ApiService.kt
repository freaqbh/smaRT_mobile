package com.capstone.smart.data.remote

import com.capstone.smart.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ══════════════════════════════════════════════
    // AUTH
    // ══════════════════════════════════════════════

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<MessageResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    // ══════════════════════════════════════════════
    // BROADCAST
    // ══════════════════════════════════════════════

    @GET("broadcast")
    suspend fun getBroadcasts(
        @Query("limit") limit: Int = 10
    ): Response<BroadcastListResponse>

    // ══════════════════════════════════════════════
    // SURAT PENGANTAR
    // ══════════════════════════════════════════════

    @GET("surat")
    suspend fun getSuratList(): Response<SuratListResponse>

    @Multipart
    @POST("surat/ajukan")
    suspend fun ajukanSurat(
        @Part("nama_surat") namaSurat: RequestBody,
        @Part("deskripsi_surat") deskripsiSurat: RequestBody,
        @Part dokumenPendukung: MultipartBody.Part? = null
    ): Response<SuratResponse>

    // ══════════════════════════════════════════════
    // PANIC BUTTON
    // ══════════════════════════════════════════════

    @POST("panic/trigger")
    suspend fun triggerPanic(
        @Body request: PanicRequest
    ): Response<PanicResponse>

    // ══════════════════════════════════════════════
    // KAS / KEUANGAN
    // ══════════════════════════════════════════════

    @GET("kas/history")
    suspend fun getKasHistory(): Response<KasHistoryResponse>

    @GET("kas/monitor")
    suspend fun getKasMonitor(): Response<KasMonitorResponse>
}

package com.capstone.smart.data.model

import com.google.gson.annotations.SerializedName

// ══════════════════════════════════════════════
// AUTH
// ══════════════════════════════════════════════

data class LoginRequest(
    val NIK: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val id_rt: String?,
    val nama: String,
    val NIK: String,
    val role: String,
    val phone: String?,
    val created_at: String?
)

data class MessageResponse(
    val message: String
)

// ══════════════════════════════════════════════
// BROADCAST
// ══════════════════════════════════════════════

data class BroadcastListResponse(
    val data: List<Broadcast>
)

data class Broadcast(
    val id: String,
    val pengurus_id: String?,
    val judul: String,
    val isi_pesan: String,
    val kategori: String,
    val tanggal_kegiatan: String?,
    val waktu_kegiatan: String?,
    val lokasi: String?,
    val created_at: String?,
    val pengurus: BroadcastPengurus?
)

data class BroadcastPengurus(
    val id: String,
    val nama: String
)

// ══════════════════════════════════════════════
// SURAT PENGANTAR
// ══════════════════════════════════════════════

data class SuratListResponse(
    val data: List<Surat>
)

data class SuratResponse(
    val message: String,
    val data: Surat
)

data class Surat(
    val id: String,
    val user_id: String?,
    val nama_surat: String,
    val deskripsi_surat: String?,
    val status: String,
    val dokumen_pendukung: String?,
    val file_final: String?,
    val created_at: String?,
    val user: SuratUser?
)

data class SuratUser(
    val nama: String
)

data class SuratRiwayatResponse(
    val message: String,
    val user: SuratUser?,
    val data: List<Surat>
)

// ══════════════════════════════════════════════
// PANIC BUTTON
// ══════════════════════════════════════════════

data class PanicRequest(
    val latitude: String,
    val longitude: String
)

data class PanicResponse(
    val message: String,
    val data: PanicLog
)

data class PanicLog(
    val id: String,
    val user_id: String?,
    val latitude: String,
    val longitude: String,
    val created_at: String?,
    val user: PanicUser?
)

data class PanicUser(
    val id: String,
    val nama: String,
    val phone: String?,
    val id_rt: String?
)

// ══════════════════════════════════════════════
// KAS / KEUANGAN
// ══════════════════════════════════════════════

data class KasHistoryResponse(
    val data: List<KasTransaction>
)

data class KasTransaction(
    val id: String,
    val bendahara_id: String?,
    val jenis_kas: String,
    val nominal: Long,
    val keterangan: String,
    val previous_hash: String?,
    val current_hash: String?,
    val created_at: String?,
    val bendahara: KasBendahara?
)

data class KasBendahara(
    val id: String,
    val nama: String
)

data class KasMonitorResponse(
    val total_blocks: Int,
    val total_pemasukan: Long,
    val total_pengeluaran: Long,
    val saldo: Long,
    val last_transaction_id: String?,
    val last_block_hash: String?,
    val status_integritas: Boolean,
    val server_timestamp: String?
)

// ══════════════════════════════════════════════
// FCM TOKEN
// ══════════════════════════════════════════════

data class FcmTokenRequest(
    val token: String,
    @SerializedName("device_id")
    val deviceId: String
)

// ══════════════════════════════════════════════
// LAPORAN WARGA
// ══════════════════════════════════════════════

data class LaporanResponse(
    val message: String,
    val data: LaporanWarga
)

data class LaporanRiwayatResponse(
    val message: String,
    val user: LaporanUser?,
    val data: List<LaporanWarga>
)

data class LaporanWarga(
    val id: String,
    val user_id: String?,
    val kategori_masalah: String,
    val deskripsi: String,
    val lokasi: String?,
    val foto: String?,
    val status: String,
    val created_at: String?,
    val user: LaporanUser?
)

data class LaporanUser(
    val id: String,
    val nama: String
)

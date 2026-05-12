# 📘 smaRT Backend — API Documentation

> **Base URL:** `http://localhost:8000/api`
> **Auth:** JWT Bearer Token (header `Authorization: Bearer <token>`)
> **Content-Type:** `application/json` (kecuali upload file menggunakan `multipart/form-data`)

---

## 📑 Daftar Isi

1. [Autentikasi](#1-autentikasi)
2. [Dashboard](#2-dashboard)
3. [Manajemen Warga](#3-manajemen-warga)
4. [Surat Pengantar](#4-surat-pengantar)
5. [Panic Button](#5-panic-button)
6. [Kas / Blockchain](#6-kas--blockchain)
7. [Broadcast](#7-broadcast)
8. [Log Aktivitas Sistem](#8-log-aktivitas-sistem)
9. [Error Responses](#9-error-responses-global)
10. [Data Models](#10-data-models)

---

## Roles

| Role | Kode |
|---|---|
| Warga | `WARGA` |
| Pengurus RT | `PENGURUS` |
| Ketua RT | `KETUA` |
| Bendahara | `BENDAHARA` |

---

## 1. Autentikasi

### 1.1 Login

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/login` |
| **Auth** | ❌ Tidak perlu |

**Request Body:**

```json
{
  "NIK": "3201234567890001",
  "password": "rahasia123"
}
```

**✅ Success Response (200):**

```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "id_rt": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "nama": "Budi Santoso",
    "NIK": "3201234567890001",
    "role": "WARGA",
    "phone": "08123456789",
    "created_at": "2026-04-28T00:00:00.000000Z"
  }
}
```

**❌ Error — Kredensial salah (401):**

```json
{
  "message": "NIK atau password salah."
}
```

**❌ Error — Validasi (422):**

```json
{
  "errors": {
    "NIK": ["The NIK field is required."],
    "password": ["The password field is required."]
  }
}
```

---

### 1.2 Register

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/register` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**Request Body:**

```json
{
  "id_rt": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "nama": "Siti Aminah",
  "NIK": "3201234567890002",
  "role": "WARGA",
  "phone": "08198765432",
  "password": "password123"
}
```

| Field | Type | Required | Keterangan |
|---|---|---|---|
| `id_rt` | uuid | ✅ | Harus ada di tabel `rt` |
| `nama` | string | ✅ | Maks 255 karakter |
| `NIK` | string | ✅ | Harus unik |
| `role` | enum | ✅ | `WARGA`, `PENGURUS`, atau `BENDAHARA` |
| `phone` | string | ❌ | Maks 20 karakter |
| `password` | string | ✅ | Min 6 karakter |

**✅ Success Response (201):**

```json
{
  "message": "Akun berhasil dibuat.",
  "user": {
    "id": "f1e2d3c4-b5a6-7890-1234-567890abcdef",
    "id_rt": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "nama": "Siti Aminah",
    "NIK": "3201234567890002",
    "role": "WARGA",
    "phone": "08198765432",
    "created_at": "2026-05-03T10:00:00.000000Z"
  }
}
```

**❌ Error — NIK sudah terdaftar (409):**

```json
{
  "message": "Conflict — NIK sudah terdaftar dalam sistem."
}
```

---

### 1.3 Logout

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/logout` |
| **Auth** | ✅ JWT |
| **Role** | Semua |

**Request Body:** Tidak ada

**✅ Success Response (200):**

```json
{
  "message": "Berhasil logout."
}
```

---

### 1.4 Refresh Token

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/refresh` |
| **Auth** | ✅ JWT |
| **Role** | Semua |

**Request Body:** Tidak ada

**✅ Success Response (200):**

```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...<token_baru>",
  "user": {
    "id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "id_rt": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "nama": "Budi Santoso",
    "NIK": "3201234567890001",
    "role": "WARGA",
    "phone": "08123456789",
    "created_at": "2026-04-28T00:00:00.000000Z"
  }
}
```

---

## 2. Dashboard

### 2.1 Get Dashboard Data

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/dashboard` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**Request Body:** Tidak ada

**✅ Success Response (200):**

```json
{
  "message": "Dashboard data retrieved successfully",
  "data": {
    "users": { "warga": 150, "pengurus": 5, "total": 155 },
    "surat": { "total": 10, "pending": 2, "diproses": 3, "selesai": 4, "ditolak": 1 },
    "kas_summary": { "saldo": 1000000 },
    "recent_activities": [ ],
    "recent_surat": [ ],
    "recent_agenda": [ ],
    "laporan_warga": [ ],
    "blocks": [ ]
  }
}
```

---

## 3. Manajemen Warga

### 7.1 Daftar Warga

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/warga` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "uuid",
      "nama": "Siti Aminah",
      "NIK": "3201...",
      "role": "WARGA",
      "phone": "0819...",
      "rt": { "id_rt": "uuid", "nama_rt": "RT 01" }
    }
  ]
}
```

### 3.2 Hapus Warga

| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/warga/{id}` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**✅ Success Response (200):**

```json
{
  "message": "Akun warga berhasil dihapus."
}
```

---

## 4. Surat Pengantar

### 6.1 Lihat Daftar Pengajuan Surat

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/surat` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "uuid",
      "nama_surat": "Surat Keterangan Usaha",
      "status": "PENDING",
      "user": { "nama": "Budi" }
    }
  ]
}
```

---

### 6.2 Ajukan Surat

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/surat/ajukan` |
| **Auth** | ✅ JWT |
| **Role** | `WARGA` |
| **Content-Type** | `multipart/form-data` |

**Request Body (FormData):**

| Field | Type | Required | Keterangan |
|---|---|---|---|
| `nama_surat` | string | ✅ | Maks 255 karakter |
| `deskripsi_surat` | string | ✅ | Deskripsi kebutuhan surat |
| `dokumen_pendukung` | file | ❌ | File lampiran pendukung (PDF, JPG, PNG) |

**✅ Success Response (201):**

```json
{
  "message": "Surat berhasil diajukan.",
  "data": {
    "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
    "user_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "nama_surat": "Surat Keterangan Domisili",
    "deskripsi_surat": "Untuk keperluan administrasi kantor",
    "status": "PENDING",
    "dokumen_pendukung": "https://example.com/ktp_scan.pdf",
    "file_final": null,
    "created_at": "2026-05-03T10:30:00.000000Z"
  }
}
```

---

### 6.3 Review Surat (Approve / Reject)

| | |
|---|---|
| **Method** | `POST` (dengan `_method=PATCH`) |
| **URL** | `/api/surat/ajukan` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |
| **Content-Type** | `multipart/form-data` |

**Request Body (FormData):**

| Field | Type | Required | Keterangan |
|---|---|---|---|
| `_method` | string | ✅ | Harus diisi `PATCH` |
| `id` | uuid | ✅ | ID surat di tabel `pengajuan_surat` |
| `status` | enum | ✅ | `APPROVED` atau `REJECTED` |
| `file_final` | file | ❌ | File surat hasil jadi (PDF/JPG/PNG) |

**✅ Success Response (200):**

```json
{
  "message": "Status surat berhasil diperbarui.",
  "data": {
    "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
    "user_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "nama_surat": "Surat Keterangan Domisili",
    "deskripsi_surat": "Untuk keperluan administrasi kantor",
    "status": "APPROVED",
    "dokumen_pendukung": "https://example.com/ktp_scan.pdf",
    "file_final": "https://example.com/surat_final.pdf",
    "created_at": "2026-05-03T10:30:00.000000Z"
  }
}
```

**❌ Error — Surat tidak ditemukan (404):**

```json
{
  "message": "Surat dengan ID tersebut tidak ditemukan."
}
```

---

## 5. Panic Button

### 7.1 Trigger Panic

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/panic/trigger` |
| **Auth** | ✅ JWT |
| **Role** | `WARGA` |

**Request Body:**

```json
{
  "latitude": "-6.200000",
  "longitude": "106.816666"
}
```

| Field | Type | Required |
|---|---|---|
| `latitude` | string | ✅ |
| `longitude` | string | ✅ |

**✅ Success Response (200):**

```json
{
  "message": "Sinyal darurat berhasil dikirim ke seluruh RT.",
  "data": {
    "id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
    "user_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "latitude": "-6.200000",
    "longitude": "106.816666",
    "created_at": "2026-05-03T10:35:00.000000Z",
    "user": {
      "id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
      "nama": "Budi Santoso",
      "phone": "08123456789",
      "id_rt": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    }
  }
}
```

---


### 7.2 Lihat Daftar Panic Log (SOS)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/panic` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "uuid",
      "latitude": "-6.200000",
      "longitude": "106.816666",
      "created_at": "2026-05-03T10:35:00.000000Z",
      "user": {
        "id": "uuid",
        "nama": "Budi Santoso",
        "phone": "08123456789"
      }
    }
  ]
}
```

---

## 6. Kas / Blockchain

### 6.1 Input Transaksi Kas

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/kas/input` |
| **Auth** | ✅ JWT |
| **Role** | `BENDAHARA` |

**Request Body:**

```json
{
  "jenis_kas": "PEMASUKAN",
  "nominal": 500000,
  "keterangan": "Iuran bulanan warga Mei 2026"
}
```

| Field | Type | Required | Keterangan |
|---|---|---|---|
| `jenis_kas` | enum | ✅ | `PEMASUKAN` atau `PENGELUARAN` |
| `nominal` | integer | ✅ | Min 1 (dalam Rupiah) |
| `keterangan` | string | ✅ | Maks 500 karakter |

**✅ Success Response (201):**

```json
{
  "message": "Transaksi tercatat dan Hash berhasil digenerate.",
  "data": {
    "id": "d4e5f6a7-b8c9-0123-def0-456789012345",
    "bendahara_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "jenis_kas": "PEMASUKAN",
    "nominal": 500000,
    "keterangan": "Iuran bulanan warga Mei 2026",
    "previous_hash": "0000000000000000000000000000000000000000000000000000000000000000",
    "current_hash": "a3f2b1c4d5e6f7890123456789abcdef0123456789abcdef0123456789abcdef",
    "created_at": "2026-05-03T10:40:00.000000Z"
  }
}
```

---

### 6.2 Riwayat Kas (History)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/kas/history` |
| **Auth** | ✅ JWT |
| **Role** | Semua (authenticated) |

**Request Body:** Tidak ada

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "d4e5f6a7-b8c9-0123-def0-456789012345",
      "bendahara_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
      "jenis_kas": "PEMASUKAN",
      "nominal": 500000,
      "keterangan": "Iuran bulanan warga Mei 2026",
      "previous_hash": "000000000000000000000000000000000000000000000000000000000000000",
      "current_hash": "a3f2b1c4d5e6...",
      "created_at": "2026-05-03T10:40:00.000000Z",
      "bendahara": {
        "id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
        "nama": "Ahmad Bendahara"
      }
    }
  ]
}
```

> [!NOTE]
> Data diurutkan berdasarkan `created_at ASC` (kronologis). Setiap item menyertakan relasi `bendahara` (hanya `id` dan `nama`).

---

### 6.3 Monitor Integritas Hashchain

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/kas/monitor` |
| **Auth** | ✅ JWT |
| **Role** | Semua (authenticated) |

**Request Body:** Tidak ada

**✅ Success Response — Chain Valid (200):**

```json
{
  "total_blocks": 5,
  "total_pemasukan": 2500000,
  "total_pengeluaran": 750000,
  "saldo": 1750000,
  "last_transaction_id": "d4e5f6a7-b8c9-0123-def0-456789012345",
  "last_block_hash": "a3f2b1c4d5e6f7890123456789abcdef...",
  "status_integritas": true,
  "server_timestamp": "2026-05-03T10:45:00+07:00"
}
```

**⚠️ Response — Chain Tidak Valid / Tampered (200):**

```json
{
  "total_blocks": 5,
  "total_pemasukan": 0,
  "total_pengeluaran": 0,
  "saldo": 0,
  "last_transaction_id": "d4e5f6a7-b8c9-0123-def0-456789012345",
  "last_block_hash": "a3f2b1c4d5e6...",
  "status_integritas": false,
  "server_timestamp": "2026-05-03T10:45:00+07:00"
}
```

> [!IMPORTANT]
> Jika `status_integritas` bernilai `false`, berarti ada data transaksi yang telah dimanipulasi (tampered). Total pemasukan/pengeluaran hanya menghitung block yang valid sampai titik kerusakan.

**✅ Response — Belum Ada Data (200):**

```json
{
  "total_blocks": 0,
  "total_pemasukan": 0,
  "total_pengeluaran": 0,
  "saldo": 0,
  "last_transaction_id": null,
  "last_block_hash": null,
  "status_integritas": true,
  "server_timestamp": "2026-05-03T10:45:00+07:00"
}
```

---

## 7. Broadcast

### 7.1 Lihat Broadcast

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/broadcast` |
| **Auth** | ✅ JWT |
| **Role** | Semua (authenticated) |

**Query Parameters:**

| Param | Type | Default | Keterangan |
|---|---|---|---|
| `limit` | integer | `10` | Jumlah data yang ditampilkan |

**Contoh:** `GET /api/broadcast?limit=5`

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-567890123456",
      "pengurus_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
      "judul": "Kerja Bakti Minggu Depan",
      "isi_pesan": "Kerja bakti akan dilaksanakan hari Minggu, 10 Mei 2026 pukul 07:00.",
      "kategori": "KEGIATAN",
      "created_at": "2026-05-03T10:50:00.000000Z",
      "pengurus": {
        "id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
        "nama": "Pak RT Ahmad"
      }
    }
  ]
}
```

> [!NOTE]
> Data diurutkan berdasarkan `created_at DESC` (terbaru duluan).

---

### 7.2 Kirim Broadcast

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/broadcast` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**Request Body:**

```json
{
  "judul": "Pemadaman Listrik",
  "isi_pesan": "Akan ada pemadaman listrik di area RT 03 pada tanggal 5 Mei 2026.",
  "kategori": "INFORMASI"
}
```

| Field | Type | Required | Keterangan |
|---|---|---|---|
| `judul` | string | ✅ | Maks 255 karakter |
| `isi_pesan` | string | ✅ | Isi pengumuman |
| `kategori` | enum | ✅ | `INFORMASI`, `DARURAT`, atau `KEGIATAN` |

**✅ Success Response (201):**

```json
{
  "message": "Broadcast berhasil dikirim.",
  "data": {
    "id": "f6a7b8c9-d0e1-2345-f012-678901234567",
    "pengurus_id": "9e1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
    "judul": "Pemadaman Listrik",
    "isi_pesan": "Akan ada pemadaman listrik di area RT 03 pada tanggal 5 Mei 2026.",
    "kategori": "INFORMASI",
    "created_at": "2026-05-03T10:55:00.000000Z"
  }
}
```

---

---

## 8. Log Aktivitas Sistem

### 8.1 Lihat Log Sistem

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/log` |
| **Auth** | ✅ JWT |
| **Role** | `PENGURUS`, `KETUA` |

**✅ Success Response (200):**

```json
{
  "data": [
    {
      "id": "kas_1",
      "icon": "✅",
      "color": "green",
      "title": "Transaksi kas",
      "tag": "+Rp 500.000",
      "desc": "Pencatat: Ahmad Bendahara - Iuran Warga",
      "created_at": "2026-05-03T10:40:00.000000Z"
    }
  ]
}
```

---

## 9. Error Responses (Global)

### Validasi Gagal (422)

Semua endpoint yang menerima input akan mengembalikan format ini jika validasi gagal:

```json
{
  "errors": {
    "field_name": ["Pesan error validasi."]
  }
}
```

### Unauthorized (401)

Jika JWT token tidak valid, expired, atau tidak disertakan:

```json
{
  "message": "Unauthenticated."
}
```

### Forbidden (403)

Jika user tidak memiliki role yang sesuai untuk endpoint:

```json
{
  "message": "Forbidden — Anda tidak memiliki akses untuk endpoint ini."
}
```

---

## 10. Data Models

### User

```
id          : uuid (PK)
id_rt       : uuid (FK → rt.id_rt)
nama        : string
NIK         : string (unique)
role        : enum [WARGA, PENGURUS, KETUA, BENDAHARA]
phone       : string | null
password_hash : string (hidden dari response)
created_at  : timestamp
```

### RT

```
id_rt       : uuid (PK)
nama_rt     : string
alamat      : string
created_at  : timestamp
```

### Blockchain (Kas)

```
id            : uuid (PK)
bendahara_id  : uuid (FK → users.id)
jenis_kas     : enum [PEMASUKAN, PENGELUARAN]
nominal       : bigint
keterangan    : string
previous_hash : string(64) — SHA-256
current_hash  : string(64) — SHA-256
created_at    : timestamp
```

### Pengajuan Surat

```
id                 : uuid (PK)
user_id            : uuid (FK → users.id)
nama_surat         : string
deskripsi_surat    : text
status             : enum [PENDING, APPROVED, REJECTED]
dokumen_pendukung  : string | null
file_final         : string | null
created_at         : timestamp
```

### Panic Log

```
id          : uuid (PK)
user_id     : uuid (FK → users.id)
latitude    : string
longitude   : string
created_at  : timestamp
```

### Broadcast

```
id           : uuid (PK)
pengurus_id  : uuid (FK → users.id)
judul        : string
isi_pesan    : text
kategori     : enum [INFORMASI, DARURAT, KEGIATAN]
created_at   : timestamp
```

### Verifikasi Log

```
id               : uuid (PK)
user_id          : uuid (FK → users.id)
id_transaksi     : uuid | null
jenis_pengecekan : enum [HASH_CHECK, INTEGRITY_CHECK]
status           : enum [VALID, INVALID]
hash_tersimpan   : string(64)
hash_terhitung   : string(64)
waktu_cek        : timestamp
```

---

## Ringkasan Endpoint

| # | Method | Endpoint | Role | Deskripsi |
|---|---|---|---|---|
| 1 | `POST` | `/api/auth/login` | Public | Login dengan NIK + password |
| 2 | `POST` | `/api/auth/register` | PENGURUS, KETUA | Daftarkan user baru |
| 3 | `POST` | `/api/auth/logout` | Semua | Invalidate JWT token |
| 4 | `POST` | `/api/auth/refresh` | Semua | Refresh JWT token |
| 5 | `GET` | `/api/dashboard` | PENGURUS, KETUA | Lihat data dashboard admin |
| 6 | `GET` | `/api/warga` | PENGURUS, KETUA | Lihat daftar warga |
| 7 | `DELETE` | `/api/warga/{id}` | PENGURUS, KETUA | Hapus akun warga |
| 8 | `GET` | `/api/surat` | PENGURUS, KETUA | Lihat daftar pengajuan surat |
| 9 | `POST` | `/api/surat/ajukan` | WARGA | Ajukan surat pengantar (multipart) |
| 10 | `PATCH` | `/api/surat/ajukan` | PENGURUS, KETUA | Approve/reject & upload surat (multipart) |
| 11 | `GET` | `/api/panic` | PENGURUS, KETUA | Lihat riwayat panic log |
| 12 | `POST` | `/api/panic/trigger` | WARGA | Kirim sinyal darurat |
| 13 | `POST` | `/api/kas/input` | BENDAHARA | Catat transaksi kas |
| 14 | `GET` | `/api/kas/history` | Semua | Lihat riwayat transaksi |
| 15 | `GET` | `/api/kas/monitor` | Semua | Cek integritas hashchain |
| 16 | `GET` | `/api/broadcast` | Semua | Lihat daftar broadcast |
| 17 | `POST` | `/api/broadcast` | PENGURUS, KETUA | Kirim broadcast baru |
| 18 | `GET` | `/api/log` | PENGURUS, KETUA | Lihat daftar aktivitas sistem |

---

## Cara Penggunaan di Frontend

### 1. Simpan Token Setelah Login

```javascript
const res = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ NIK: '320123...', password: '...' }),
});
const { token, user } = await res.json();
localStorage.setItem('token', token);
```

### 2. Gunakan Token di Setiap Request

```javascript
const res = await fetch('/api/kas/history', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'Content-Type': 'application/json',
  },
});
```

### 3. Handle Token Expired

Jika response `401`, panggil `/api/auth/refresh` untuk mendapatkan token baru. Jika refresh juga gagal, redirect ke halaman login.

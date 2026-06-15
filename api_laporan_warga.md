# Dokumentasi API Laporan Warga

Dokumentasi ini berisi detail endpoint untuk fitur Laporan Warga yang dapat digunakan untuk integrasi pada aplikasi mobile. 

Semua endpoint di bawah ini memerlukan otentikasi menggunakan JWT Token.

---

## Base URL
Semua request mengarah ke prefix `/api`.
Contoh: `https://domain-anda.com/api`

## Headers Global
Setiap request ke endpoint di bawah ini **wajib** menyertakan headers berikut:
```http
Authorization: Bearer {your_jwt_token}
Accept: application/json
```

---

## 1. Buat Laporan Baru (WARGA)
Endpoint untuk mengirim laporan masalah oleh warga.

- **URL**: `/api/laporan`
- **Method**: `POST`
- **Role yang diizinkan**: `WARGA`
- **Content-Type**: `multipart/form-data` *(karena ada upload file gambar)*

### Request Parameters (Form Data)
| Parameter | Tipe | Wajib | Keterangan |
| :--- | :--- | :--- | :--- |
| `kategori_masalah` | `string` | Ya | Kategori masalah laporan (maksimal 255 karakter). |
| `deskripsi` | `string` | Ya | Penjelasan detail mengenai masalah yang dilaporkan. |
| `lokasi` | `string` | Ya | Lokasi spesifik masalah (contoh: "Blok A No. 12" atau koordinat). |
| `foto` | `file` | Tidak | Foto bukti laporan. Format: `jpg, jpeg, png`. Maksimal ukuran: `5MB`. |

### Response: 201 Created (Success)
```json
{
    "message": "Laporan berhasil dikirim.",
    "data": {
        "id": "uuid-laporan",
        "user_id": "uuid-user",
        "kategori_masalah": "Infrastruktur",
        "deskripsi": "Lampu jalan mati di depan blok B",
        "lokasi": "Jalan Utama Blok B",
        "foto": "laporan_foto/random-string.jpg",
        "status": "DIPROSES",
        "created_at": "2026-05-26T10:00:00.000000Z"
    }
}
```

### Response: 422 Unprocessable Entity (Validation Error)
```json
{
    "errors": {
        "kategori_masalah": ["The kategori masalah field is required."],
        "deskripsi": ["The deskripsi field is required."],
        "lokasi": ["The lokasi field is required."],
        "foto": ["The foto failed to upload.", "The foto must be a file of type: jpg, jpeg, png.", "The foto must not be greater than 5120 kilobytes."]
    }
}
```

---

## 2. Riwayat Laporan Warga
Endpoint untuk mengambil seluruh riwayat laporan yang pernah dibuat oleh user (warga) tertentu.

- **URL**: `/api/laporan/{user_id}/riwayat`
- **Method**: `GET`
- **Role yang diizinkan**: Semua Role (Selama JWT Valid)

### Path Variables
| Parameter | Tipe | Wajib | Keterangan |
| :--- | :--- | :--- | :--- |
| `user_id` | `uuid` | Ya | ID (UUID) dari user yang ingin dilihat riwayat laporannya. Biasanya ini adalah UUID dari user yang sedang login di mobile app. |

### Response: 200 OK (Success)
```json
{
    "message": "Riwayat laporan berhasil diambil.",
    "user": {
        "id": "uuid-user",
        "nama": "Budi Santoso"
    },
    "data": [
        {
            "id": "uuid-laporan",
            "user_id": "uuid-user",
            "kategori_masalah": "Infrastruktur",
            "deskripsi": "Lampu jalan mati di depan blok B",
            "lokasi": "Jalan Utama Blok B",
            "foto": "laporan_foto/random-string.jpg",
            "status": "DIPROSES",
            "created_at": "2026-05-26T10:00:00.000000Z"
        }
    ]
}
```

### Response: 404 Not Found
```json
{
    "message": "User tidak ditemukan."
}
```

---

## 3. Ambil Semua Laporan (PENGURUS / KETUA)
Endpoint untuk mengambil data seluruh laporan warga. Biasanya digunakan pada dashboard pengurus, namun bisa juga diimplementasikan jika aplikasi mobile memiliki role admin/pengurus.

- **URL**: `/api/laporan`
- **Method**: `GET`
- **Role yang diizinkan**: `PENGURUS`, `KETUA`

### Response: 200 OK (Success)
```json
{
    "data": [
        {
            "id": "uuid-laporan",
            "user_id": "uuid-user",
            "kategori_masalah": "Keamanan",
            "deskripsi": "Ada orang mencurigakan di taman",
            "lokasi": "Taman Warga",
            "foto": null,
            "status": "SELESAI",
            "created_at": "2026-05-26T09:00:00.000000Z",
            "user": {
                "id": "uuid-user",
                "nama": "Siti Aminah"
            }
        }
    ]
}
```

---

## 4. Update Status Laporan (PENGURUS / KETUA)
Endpoint untuk mengubah status laporan (misalnya dari `DIPROSES` menjadi `SELESAI`).

- **URL**: `/api/laporan/{id_laporan}/status`
- **Method**: `PATCH`
- **Role yang diizinkan**: `PENGURUS`, `KETUA`
- **Content-Type**: `application/json` atau `application/x-www-form-urlencoded`

### Path Variables
| Parameter | Tipe | Wajib | Keterangan |
| :--- | :--- | :--- | :--- |
| `id_laporan` | `uuid` | Ya | ID (UUID) dari laporan yang akan diupdate. |

### Request Body (JSON)
```json
{
    "status": "SELESAI"
}
```
*Catatan: Value status yang valid adalah `DIPROSES` atau `SELESAI`.*

### Response: 200 OK (Success)
```json
{
    "message": "Status laporan berhasil diperbarui.",
    "data": {
        "id": "uuid-laporan",
        "user_id": "uuid-user",
        "kategori_masalah": "Keamanan",
        "deskripsi": "Ada orang mencurigakan di taman",
        "lokasi": "Taman Warga",
        "foto": null,
        "status": "SELESAI",
        "created_at": "2026-05-26T09:00:00.000000Z"
    }
}
```

### Response: 404 Not Found
```json
{
    "message": "Laporan tidak ditemukan."
}
```

### Response: 422 Unprocessable Entity (Validation Error)
```json
{
    "errors": {
        "status": ["The selected status is invalid."]
    }
}
```

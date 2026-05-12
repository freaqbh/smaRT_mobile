# 🏘️ smaRT (Sistem Manajemen RT)

smaRT adalah sebuah sistem informasi manajemen Rukun Tetangga (RT) yang dibangun dengan menggunakan **Laravel 13** (sebagai Backend API) dan **Vue 3** (sebagai Frontend). Sistem ini bertujuan untuk mendigitalisasi dan mempermudah berbagai proses administrasi, pelaporan, komunikasi, dan pengelolaan keuangan (kas) di tingkat RT.

Sistem ini menerapkan perlindungan data mutakhir melalui fitur **SHA-256 Hashchain Ledger** untuk memastikan integritas dan transparansi catatan kas/keuangan RT (Blockchain-based ledger).

---

## 🌟 Fitur Utama

- **🔐 Autentikasi & Role-Based Access Control (RBAC):** Menggunakan JWT (JSON Web Token) dengan pembatasan hak akses berbasis role: `WARGA`, `PENGURUS`, `KETUA`, dan `BENDAHARA`.
- **📊 Admin Dashboard:** Antarmuka interaktif dengan tema gelap (dark-theme) yang responsif. Menampilkan metrik utama seperti statistik warga, status pengajuan surat, ringkasan saldo kas, dan aktivitas sistem terbaru.
- **👥 Manajemen Warga:** Pencatatan dan pengelolaan data kependudukan warga oleh pengurus.
- **✉️ Manajemen Surat Pengantar:** Warga dapat mengajukan permohonan surat pengantar secara mandiri (online) dengan melampirkan dokumen pendukung. Pengurus dapat meninjau (review), menyetujui (approve), atau menolak (reject) permohonan secara terpusat.
- **🚨 Panic Button (Sistem Darurat):** Fitur SOS mandiri untuk warga yang berada dalam kondisi darurat. Sistem akan merekam koordinat lokasi (latitude & longitude) secara akurat dan menyiarkan peringatan kepada pengurus dan warga lain secara real-time.
- **🔗 Keuangan / Kas RT (Blockchain):** Pencatatan sirkulasi pemasukan dan pengeluaran kas menggunakan teknik *hashchain* (SHA-256) untuk mencegah manipulasi data (tamper-proof). Terdapat modul pengawasan (monitor) yang bisa memvalidasi integritas ledger secara independen dan otomatis.
- **📢 Broadcast Pengumuman:** Pengurus dapat mengirimkan informasi, peringatan darurat, atau jadwal kegiatan secara serentak (broadcast) kepada seluruh warga.
- **📝 Log Aktivitas Sistem:** Pencatatan aktivitas operasional aplikasi yang historis (audit trail) untuk mempermudah pemantauan (monitoring) terpadu.

---

## 🛠️ Teknologi yang Digunakan

### Backend (`smaRT-backend`)
- **Framework:** Laravel 13 (API Only)
- **Language:** PHP 8.3+
- **Database:** MySQL
- **Authentication:** `php-open-source-saver/jwt-auth` (JWT)
- **Security:** SHA-256 Cryptographic Hashchain untuk tabel Kas (`blockchain`)

### Frontend (`smaRT-frontend`)
- **Framework:** Vue 3 (Composition API)
- **Build Tool:** Vite
- **State Management:** Pinia
- **Routing:** Vue Router
- **Styling:** Tailwind CSS
- **Charts:** Chart.js & vue-chartjs
- **HTTP Client:** Axios

---

## 📂 Struktur Proyek

- `/smaRT-backend` : Source code dan konfigurasi untuk RESTful API Backend (Laravel).
- `/smaRT-frontend` : Source code untuk antarmuka pengguna Web / Dashboard (Vue.js).
- `api_documentation.md` : Dokumentasi lengkap mengenai semua endpoint API beserta format request dan responsenya.
- `scheme.md` : Dokumentasi skema database operasional (tabel dan relasi) dalam format JSON schema-style.
- `blockchain.sql` : Script inisialisasi tabel dan *stored procedures* terkait mekanisme blockchain kas.

---

## 🚀 Instalasi & Cara Menjalankan (Getting Started)

### 1. Prasyarat
Pastikan environment Anda memenuhi spesifikasi minimum berikut:
- PHP >= 8.3
- Composer
- Node.js >= 20.19.0
- MySQL Server

### 2. Setup Backend (Laravel API)

```bash
cd smaRT-backend
# 1. Install dependensi package manager PHP
composer install

# 2. Copy dan sesuaikan environment variables
cp .env.example .env

# 3. Generate Application Key dan JWT Secret Key
php artisan key:generate
php artisan jwt:secret

# 4. Buat database di MySQL (misal: smart_db) dan set kredensialnya di file .env.
# 5. Jalankan migrasi tabel database (beserta data dummy / seed jika ada)
php artisan migrate

# 6. Jalankan server lokal backend
php artisan serve
```
*Backend API akan berjalan secara default di `http://localhost:8000`.*

### 3. Setup Frontend (Vue Dashboard)

```bash
cd smaRT-frontend
# 1. Install dependensi package manager Node
npm install

# 2. Jalankan development server untuk frontend
npm run dev
```
*Frontend akan berjalan di URL lokal (biasanya di `http://localhost:5173`).*

---

## 🔗 Dokumentasi API Terintegrasi

Untuk dokumentasi lengkap dan detail dari operasional endpoint REST API—seperti rute autentikasi, sistem manajemen kas, transmisi log SOS, serta kontrol manajemen surat—silakan meninjau berkas berikut:

👉 **[api_documentation.md](./api_documentation.md)**

### Sekilas Endpoint Utama:
- `POST /api/auth/login` : Autentikasi dan penerbitan token.
- `GET /api/dashboard` : Agregasi analitik data untuk dashboard.
- `POST /api/surat/ajukan` : Pengajuan berkas surat (Mendukung `multipart/form-data`).
- `POST /api/panic/trigger` : Transmisi sinyal darurat lokasi terkini.
- `POST /api/kas/input` : Pencatatan mutasi transaksi keuangan secara kriptografis.
- `GET /api/kas/monitor` : Analisis integritas dari seluruh riwayat blok (ledger chain).

---

## 🗄️ Skema Database Terperinci

Sistem ini didesain menggunakan arsitektur basis data relasional. Untuk detail konvensi penamaan (tabel `user`, `rt`, `pengajuan_surat`, `panic-logs`, `blockchain`, `broadcast`), dan batasan tipe data (constraints), baca berkas ini:

👉 **[scheme.md](./scheme.md)**

---

© 2026 smaRT (Sistem Manajemen Rukun Tetangga). All Rights Reserved.

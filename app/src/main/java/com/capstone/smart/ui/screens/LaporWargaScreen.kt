package com.capstone.smart.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.capstone.smart.ui.components.*
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporWargaScreen(
    viewModel: SmaRTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Form state
    var expandedKategori by remember { mutableStateOf(false) }
    val optionsKategori = listOf("Keamanan (Siskamling)", "Kebersihan & Lingkungan", "Infrastruktur", "Sosial & Warga", "Lainnya")
    var selectedKategori by remember { mutableStateOf(optionsKategori[0]) }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }

    // Photo state
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoFileName by remember { mutableStateOf<String?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            selectedPhotoUri = tempPhotoUri
            selectedPhotoFileName = "foto_laporan.jpg"
        }
    }

    // Camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoFile = File.createTempFile("laporan_", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
            tempPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            selectedPhotoFileName = getFileNameFromUri(context, uri)
        }
    }

    // Load riwayat
    LaunchedEffect(Unit) {
        viewModel.loadLaporanRiwayat()
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.laporanSubmitResult) {
        viewModel.laporanSubmitResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearLaporanSubmitResult()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
        ) {
            SmaRTHeader(userName = viewModel.currentUser?.nama)

            Spacer(modifier = Modifier.height(16.dp))

            // Tab toggle
            TabToggle(
                tabs = listOf("Buat Laporan", "Riwayat"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = LaporOrange,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                // ═══ BUAT LAPORAN FORM ═══

                // Kategori Masalah
                SectionTitle(
                    text = "KATEGORI MASALAH",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedKategori,
                    onExpandedChange = { expandedKategori = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    OutlinedTextField(
                        value = selectedKategori,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DividerGray,
                            focusedBorderColor = LaporOrange,
                            unfocusedContainerColor = CardWhite,
                            focusedContainerColor = CardWhite
                        ),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expandedKategori,
                        onDismissRequest = { expandedKategori = false },
                        modifier = Modifier.background(CardWhite)
                    ) {
                        optionsKategori.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedKategori = option
                                    expandedKategori = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Deskripsi
                SectionTitle(
                    text = "DESKRIPSI",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    placeholder = {
                        Text(
                            "Contoh: Lampu jalan di depan blok C-07 mati sejak 3 hari lalu...",
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DividerGray,
                        focusedBorderColor = LaporOrange,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Lokasi
                SectionTitle(
                    text = "LOKASI",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { lokasi = it },
                    placeholder = {
                        Text(
                            "Contoh: Jalan Utama Blok B, dekat pos ronda",
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DividerGray,
                        focusedBorderColor = LaporOrange,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Foto (Opsional)
                SectionTitle(
                    text = "FOTO (OPSIONAL)",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (selectedPhotoUri != null) {
                    // Preview foto yang sudah dipilih
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Foto laporan",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = {
                                    selectedPhotoUri = null
                                    selectedPhotoFileName = null
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Hapus foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (selectedPhotoFileName != null) {
                            Text(
                                text = selectedPhotoFileName!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    // Tombol ambil foto / galeri
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Kamera
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = DividerGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(CardWhite)
                                .clickable {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = "Ambil foto",
                                    tint = LaporOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Kamera",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Galeri
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = DividerGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(CardWhite)
                                .clickable {
                                    galleryLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = "Pilih dari galeri",
                                    tint = LaporOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Galeri",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        if (deskripsi.isNotBlank() && lokasi.isNotBlank()) {
                            val fotoFile = selectedPhotoUri?.let { uri ->
                                uriToTempFile(context, uri)
                            }
                            viewModel.kirimLaporan(selectedKategori, deskripsi, lokasi, fotoFile)
                            // Reset form
                            deskripsi = ""
                            lokasi = ""
                            selectedKategori = optionsKategori[0]
                            selectedPhotoUri = null
                            selectedPhotoFileName = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LaporOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    enabled = !viewModel.laporanSubmitLoading && deskripsi.isNotBlank() && lokasi.isNotBlank()
                ) {
                    if (viewModel.laporanSubmitLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Kirim Laporan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Status Terakhir (preview dari riwayat)
                if (viewModel.laporanRiwayat.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionTitle(
                        text = "LAPORAN TERAKHIR",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    viewModel.laporanRiwayat.take(2).forEach { laporan ->
                        val statusColor = when (laporan.status) {
                            "SELESAI" -> StatusGreen
                            "DIPROSES" -> StatusBlue
                            else -> StatusOrange
                        }
                        val statusText = when (laporan.status) {
                            "SELESAI" -> "Selesai"
                            "DIPROSES" -> "Diproses"
                            else -> laporan.status
                        }
                        LaporanCard(
                            title = laporan.deskripsi.take(50) + if (laporan.deskripsi.length > 50) "..." else "",
                            category = laporan.kategori_masalah,
                            lokasi = laporan.lokasi,
                            time = laporan.created_at?.take(10) ?: "-",
                            status = statusText,
                            statusColor = statusColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } else {
                // ═══ RIWAYAT TAB ═══
                if (viewModel.laporanRiwayatLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LaporOrange, modifier = Modifier.size(28.dp))
                    }
                } else if (viewModel.laporanRiwayat.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat laporan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                } else {
                    viewModel.laporanRiwayat.forEach { laporan ->
                        val statusColor = when (laporan.status) {
                            "SELESAI" -> StatusGreen
                            "DIPROSES" -> StatusBlue
                            else -> StatusOrange
                        }
                        val statusText = when (laporan.status) {
                            "SELESAI" -> "Selesai"
                            "DIPROSES" -> "Diproses"
                            else -> laporan.status
                        }
                        LaporanCard(
                            title = laporan.deskripsi.take(50) + if (laporan.deskripsi.length > 50) "..." else "",
                            category = laporan.kategori_masalah,
                            lokasi = laporan.lokasi,
                            time = laporan.created_at?.take(10) ?: "-",
                            status = statusText,
                            statusColor = statusColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        )
    }
}

@Composable
private fun LaporanCard(
    title: String,
    category: String,
    lokasi: String?,
    time: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$category · $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                if (!lokasi.isNullOrBlank()) {
                    Text(
                        text = "📍 $lokasi",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(text = status, color = statusColor)
        }
    }
}

/**
 * Get filename from a content URI.
 */
private fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "foto_laporan.jpg"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}

/**
 * Copy content URI to a temporary file for upload.
 * Ensures the file has a proper image extension (.jpg/.png) for backend validation.
 */
private fun uriToTempFile(context: Context, uri: Uri): File? {
    return try {
        // Detect MIME type and assign correct extension
        val mimeType = context.contentResolver.getType(uri)
        val extension = when {
            mimeType?.contains("png") == true -> ".png"
            mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> ".jpg"
            else -> ".jpg" // Default to jpg
        }
        val tempFile = File(context.cacheDir, "laporan_foto_${System.currentTimeMillis()}$extension")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}

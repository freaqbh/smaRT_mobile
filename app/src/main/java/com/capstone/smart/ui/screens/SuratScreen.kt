package com.capstone.smart.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.smart.ui.components.*
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratScreen(
    viewModel: SmaRTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var expandedSurat by remember { mutableStateOf(false) }
    val optionsSurat = listOf("Pengantar SKCK", "Surat Domisili", "Surat Keterangan Usaha","Surat Keterangan Ahli Waris",
    "Surat Keterangan Pindah Nikah",
    "Surat Keterangan Domisili Usaha",
    "Surat Pengesahan Tanda Bukti Diri(Taspen)", "Lainnya")
    var selectedSurat by remember { mutableStateOf(optionsSurat[0]) }
    var keperluan by remember { mutableStateOf("") }

    // File picker state
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        selectedFileName = uri?.let { getFileName(context, it) }
    }

    // Load surat list saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.loadSuratRiwayat()
    }

    // Snackbar untuk submit result
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.suratSubmitResult) {
        viewModel.suratSubmitResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuratSubmitResult()
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
                tabs = listOf("Buat Baru", "Riwayat"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = Teal600,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                // Buat Baru form
                SectionTitle(
                    text = "JENIS SURAT",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedSurat,
                    onExpandedChange = { expandedSurat = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    OutlinedTextField(
                        value = selectedSurat,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSurat) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DividerGray,
                            focusedBorderColor = Teal600,
                            unfocusedContainerColor = CardWhite,
                            focusedContainerColor = CardWhite
                        ),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSurat,
                        onDismissRequest = { expandedSurat = false },
                        modifier = Modifier.background(CardWhite)
                    ) {
                        optionsSurat.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSurat = option
                                    expandedSurat = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(
                    text = "KEPERLUAN",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    value = keperluan,
                    onValueChange = { keperluan = it },
                    placeholder = {
                        Text(
                            "Contoh: Untuk melamar pekerjaan di PT ABC...",
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
                        focusedBorderColor = Teal600,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dokumen Pendukung (file upload)
                SectionTitle(
                    text = "DOKUMEN PENDUKUNG (OPSIONAL)",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (selectedFileName != null) {
                    // Show selected file
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Teal100),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = null,
                                tint = Teal600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedFileName ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    selectedFileUri = null
                                    selectedFileName = null
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Hapus file",
                                    tint = TextTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Upload button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.5.dp,
                                color = DividerGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(CardWhite)
                            .clickable {
                                fileLauncher.launch("*/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = "Upload dokumen",
                                tint = TextTertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ketuk untuk upload file (PDF/JPG/PNG)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status Terakhir dari API
                if (viewModel.suratRiwayat.isNotEmpty()) {
                    SectionTitle(
                        text = "STATUS TERAKHIR",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    viewModel.suratRiwayat.take(2).forEach { surat ->
                        val statusColor = when (surat.status) {
                            "APPROVED" -> StatusGreen
                            "REJECTED" -> PanicRed
                            else -> StatusOrange
                        }
                        val statusText = when (surat.status) {
                            "APPROVED" -> "Disetujui"
                            "REJECTED" -> "Ditolak"
                            else -> "Menunggu"
                        }
                        SuratStatusCard(
                            name = surat.nama_surat,
                            date = "Diajukan ${surat.created_at?.take(10) ?: "-"}",
                            status = statusText,
                            statusColor = statusColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit button
                Button(
                    onClick = {
                        if (keperluan.isNotBlank()) {
                            val file = selectedFileUri?.let { uri ->
                                uriToFile(context, uri)
                            }
                            viewModel.ajukanSurat(selectedSurat, keperluan, file)
                            keperluan = ""
                            selectedFileUri = null
                            selectedFileName = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal600,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    enabled = !viewModel.suratSubmitLoading
                ) {
                    if (viewModel.suratSubmitLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Kirim Pengajuan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Riwayat tab — dari API
                if (viewModel.suratRiwayatLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Teal600, modifier = Modifier.size(28.dp))
                    }
                } else if (viewModel.suratRiwayat.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat surat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                } else {
                    viewModel.suratRiwayat.forEach { surat ->
                        val statusColor = when (surat.status) {
                            "APPROVED" -> StatusGreen
                            "REJECTED" -> PanicRed
                            else -> StatusOrange
                        }
                        val statusText = when (surat.status) {
                            "APPROVED" -> "Disetujui"
                            "REJECTED" -> "Ditolak"
                            else -> "Menunggu"
                        }
                        SuratStatusCard(
                            name = surat.nama_surat,
                            date = "Diajukan ${surat.created_at?.take(10) ?: "-"}",
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
private fun SuratStatusCard(
    name: String,
    date: String,
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
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            StatusBadge(text = status, color = statusColor)
        }
    }
}

/**
 * Get filename from a content URI.
 */
private fun getFileName(context: Context, uri: Uri): String {
    var name = "file"
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
 */
private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val fileName = getFileName(context, uri)
        val tempFile = File(context.cacheDir, fileName)
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

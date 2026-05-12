package com.capstone.smart.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.smart.ui.components.*
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratScreen(
    viewModel: SmaRTViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var expandedSurat by remember { mutableStateOf(false) }
    val optionsSurat = listOf("Pengantar SKCK", "Surat Domisili", "Surat Keterangan Usaha", "Lainnya")
    var selectedSurat by remember { mutableStateOf(optionsSurat[0]) }
    var keperluan by remember { mutableStateOf("") }

    // Load surat list saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.loadSuratList()
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

                Spacer(modifier = Modifier.height(24.dp))

                // Status Terakhir dari API
                if (viewModel.suratList.isNotEmpty()) {
                    SectionTitle(
                        text = "STATUS TERAKHIR",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    viewModel.suratList.take(2).forEach { surat ->
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
                            viewModel.ajukanSurat(selectedSurat, keperluan)
                            keperluan = ""
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
                if (viewModel.suratLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Teal600, modifier = Modifier.size(28.dp))
                    }
                } else if (viewModel.suratList.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat surat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                } else {
                    viewModel.suratList.forEach { surat ->
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

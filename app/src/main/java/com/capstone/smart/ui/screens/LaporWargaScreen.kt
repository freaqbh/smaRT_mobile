package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.smart.ui.components.*
import com.capstone.smart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporWargaScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader()

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
            // Buat Laporan form
            SectionTitle(
                text = "KATEGORI MASALAH",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            var expandedKategori by remember { mutableStateOf(false) }
            val optionsKategori = listOf("Keamanan (Siskamling)", "Kebersihan & Lingkungan", "Infrastruktur", "Sosial & Warga", "Lainnya")
            var selectedKategori by remember { mutableStateOf(optionsKategori[0]) }

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

            SectionTitle(
                text = "DESKRIPSI & LOKASI",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            var deskripsi by remember { mutableStateOf("") }
            
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

            SectionTitle(
                text = "FOTO (OPSIONAL)",
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Photo placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.5.dp,
                        color = DividerGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(CardWhite),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Ambil foto",
                        tint = TextTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ketuk untuk ambil foto",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LaporOrange,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = "Kirim Laporan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Laporan Aktif
            SectionTitle(
                text = "LAPORAN AKTIF",
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            LaporanCard(
                title = "Lampu jalan mati",
                category = "Keamanan",
                time = "2 hari lalu",
                status = "Diproses",
                statusColor = StatusBlue
            )
        } else {
            // Riwayat tab
            LaporanCard(
                title = "Lampu jalan mati",
                category = "Keamanan",
                time = "2 hari lalu",
                status = "Diproses",
                statusColor = StatusBlue
            )
            Spacer(modifier = Modifier.height(10.dp))
            LaporanCard(
                title = "Sampah menumpuk",
                category = "Kebersihan",
                time = "1 minggu lalu",
                status = "Selesai",
                statusColor = StatusGreen
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun LaporanCard(
    title: String,
    category: String,
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
            Column {
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
            }
            StatusBadge(text = status, color = statusColor)
        }
    }
}

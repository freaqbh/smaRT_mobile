package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.smart.ui.components.SmaRTHeader
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@Composable
fun AgendaScreen(
    viewModel: SmaRTViewModel,
    modifier: Modifier = Modifier
) {
    // Track attendance per agenda item (by ID)
    val attendanceMap = remember { mutableStateMapOf<String, Boolean?>() }

    // Load agenda from backend
    LaunchedEffect(Unit) {
        viewModel.loadAgenda()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader(userName = viewModel.currentUser?.nama)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Agenda Kegiatan RT",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.agendaLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AgendaPurple, modifier = Modifier.size(32.dp))
            }
        } else if (viewModel.agendaList.isEmpty()) {
            Text(
                text = "Belum ada agenda kegiatan.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        } else {
            viewModel.agendaList.forEachIndexed { index, agenda ->
                // Parse date from tanggal_kegiatan
                val dateStr = agenda.tanggal_kegiatan?.take(10) ?: agenda.created_at?.take(10) ?: ""
                val dateParts = dateStr.split("-")
                val month = if (dateParts.size >= 2) {
                    when (dateParts[1]) {
                        "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"
                        "04" -> "APR"; "05" -> "MEI"; "06" -> "JUN"
                        "07" -> "JUL"; "08" -> "AGU"; "09" -> "SEP"
                        "10" -> "OKT"; "11" -> "NOV"; "12" -> "DES"
                        else -> "---"
                    }
                } else "---"
                val day = if (dateParts.size >= 3) dateParts[2] else "--"

                val attendanceState = attendanceMap[agenda.id]

                AgendaEventCard(
                    month = month,
                    day = day,
                    dateColor = if (index % 2 == 0) AgendaPurple else AgendaPink,
                    title = agenda.judul,
                    description = agenda.isi_pesan,
                    postedBy = agenda.pengurus?.nama ?: "",
                    buttonColor = AgendaPurple,
                    waktu = agenda.waktu_kegiatan,
                    lokasi = agenda.lokasi,
                    attendanceState = attendanceState,
                    onHadir = { attendanceMap[agenda.id] = true },
                    onTidakBisa = { attendanceMap[agenda.id] = false }
                )

                if (index < viewModel.agendaList.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun AgendaEventCard(
    month: String,
    day: String,
    dateColor: Color,
    title: String,
    description: String,
    postedBy: String,
    buttonColor: Color,
    waktu: String?,
    lokasi: String?,
    attendanceState: Boolean?,
    onHadir: () -> Unit,
    onTidakBisa: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Date block
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(dateColor)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = day,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Event details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 3
                )

                if (postedBy.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Oleh: $postedBy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                
                if (!waktu.isNullOrBlank() || !lokasi.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val info = mutableListOf<String>()
                    if (!waktu.isNullOrBlank()) info.add("🕒 ${waktu.take(5)}")
                    if (!lokasi.isNullOrBlank()) info.add("📍 $lokasi")
                    Text(
                        text = info.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Attendance status display
                if (attendanceState != null) {
                    val statusText = if (attendanceState) "✅ Anda akan hadir" else "❌ Anda tidak bisa hadir"
                    val statusColor = if (attendanceState) StatusGreen else TextTertiary

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    // RSVP buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onHadir,
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                text = "Saya Hadir",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        OutlinedButton(
                            onClick = onTidakBisa,
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, DividerGray
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Tidak Bisa",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

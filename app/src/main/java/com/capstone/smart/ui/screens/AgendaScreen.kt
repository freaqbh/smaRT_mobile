package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.smart.ui.components.SmaRTHeader
import com.capstone.smart.ui.theme.*

@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Agenda Kegiatan RT",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Event 1
        AgendaEventCard(
            month = "MAR",
            day = "05",
            weekday = "Minggu",
            dateColor = AgendaPurple,
            title = "Kerja Bakti Saluran Air",
            badge = "Wajib",
            badgeColor = StatusRed,
            time = "07:00 – Selesai",
            location = "Lingkungan RT 03",
            buttonColor = AgendaPurple
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Event 2
        AgendaEventCard(
            month = "MAR",
            day = "07",
            weekday = "Selasa",
            dateColor = AgendaPurple,
            title = "Posyandu Balita",
            badge = "Kesehatan",
            badgeColor = StatusGreen,
            time = "09:00 – 12:00",
            location = "Balai RW 04",
            buttonColor = AgendaPurple
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Event 3
        AgendaEventCard(
            month = "MAR",
            day = "11",
            weekday = "Sabtu",
            dateColor = AgendaPink,
            title = "Rapat Rutin Pengurus",
            badge = "Undangan",
            badgeColor = AgendaPurple,
            time = "19:30 – Selesai",
            location = "Rumah Pak RT",
            buttonColor = AgendaPurple
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun AgendaEventCard(
    month: String,
    day: String,
    weekday: String,
    dateColor: Color,
    title: String,
    badge: String,
    badgeColor: Color,
    time: String,
    location: String,
    buttonColor: Color,
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
                    Text(
                        text = weekday,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Event details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = badgeColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // RSVP buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {},
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
                        onClick = {},
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

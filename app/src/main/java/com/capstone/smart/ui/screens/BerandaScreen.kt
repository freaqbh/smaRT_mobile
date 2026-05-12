package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.smart.ui.components.SectionTitle
import com.capstone.smart.ui.components.SmaRTHeader
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@Composable
fun BerandaScreen(
    viewModel: SmaRTViewModel,
    onPanicClick: () -> Unit,
    onNavigateToSurat: () -> Unit,
    onNavigateToKeuangan: () -> Unit,
    onNavigateToLapor: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Load broadcasts saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.loadBroadcasts()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader(userName = viewModel.currentUser?.nama)

        Spacer(modifier = Modifier.height(16.dp))

        // Panic Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { onPanicClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanicOrange),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "PANIC BUTTON",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tekan untuk keadaan darurat",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Layanan Utama
        SectionTitle(
            text = "LAYANAN UTAMA",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ServiceCard(
                title = "Surat Pengantar",
                icon = Icons.Outlined.Description,
                iconBg = Teal100,
                iconTint = Teal600,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSurat
            )
            ServiceCard(
                title = "Keuangan RT",
                icon = Icons.Outlined.AttachMoney,
                iconBg = Teal100,
                iconTint = Teal600,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToKeuangan
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ServiceCard(
                title = "Lapor Warga",
                icon = Icons.Outlined.ReportProblem,
                iconBg = Color(0xFFE8F5E9),
                iconTint = StatusGreen,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToLapor
            )
            ServiceCard(
                title = "Agenda Kegiatan",
                icon = Icons.Outlined.Event,
                iconBg = Color(0xFFE8F5E9),
                iconTint = StatusGreen,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAgenda
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Informasi Terkini — dari API Broadcast
        SectionTitle(
            text = "INFORMASI TERKINI",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        if (viewModel.broadcastLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Teal600, modifier = Modifier.size(28.dp))
            }
        } else if (viewModel.broadcasts.isEmpty()) {
            Text(
                text = "Belum ada informasi terkini.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        } else {
            viewModel.broadcasts.forEachIndexed { index, broadcast ->
                val borderColor = when (broadcast.kategori) {
                    "DARURAT" -> PanicRed
                    "KEGIATAN" -> StatusBlue
                    else -> BorderRed
                }
                InfoCard(
                    title = broadcast.judul,
                    description = broadcast.isi_pesan,
                    time = broadcast.created_at?.take(10) ?: "",
                    borderColor = borderColor
                )
                if (index < viewModel.broadcasts.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // bottom nav spacing
    }
}

@Composable
private fun ServiceCard(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    description: String,
    time: String,
    borderColor: Color,
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
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .defaultMinSize(minHeight = 80.dp)
                    .background(
                        borderColor,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 3
                )
            }
        }
    }
}

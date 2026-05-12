package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.smart.ui.components.SmaRTHeader
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@Composable
fun AkunScreen(
    viewModel: SmaRTViewModel,
    onNavigateToAgenda: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = viewModel.currentUser
    val displayName = user?.nama ?: "Warga"
    val initials = displayName.split(" ")
        .take(2)
        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        .ifEmpty { "W" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader(userName = user?.nama)

        Spacer(modifier = Modifier.height(20.dp))

        // Profile section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Teal600),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Warga Tetap",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NIK: ${user?.NIK ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Menu items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column {
                AkunMenuItem(
                    icon = Icons.Outlined.Event,
                    title = "Agenda Kegiatan",
                    subtitle = "Lihat jadwal kegiatan RT",
                    onClick = onNavigateToAgenda
                )
                HorizontalDivider(
                    color = DividerGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                AkunMenuItem(
                    icon = Icons.Outlined.Person,
                    title = "Edit Profil",
                    subtitle = "Ubah data pribadi"
                )
                HorizontalDivider(
                    color = DividerGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                AkunMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Ubah Password",
                    subtitle = "Ganti kata sandi akun"
                )
                HorizontalDivider(
                    color = DividerGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                AkunMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "Tentang Aplikasi",
                    subtitle = "smaRT versi 1.0"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable {
                    viewModel.logout {
                        onLogout()
                    }
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            AkunMenuItem(
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                title = "Keluar",
                subtitle = "Logout dari akun",
                tint = PanicRed
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun AkunMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = Teal600,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (tint == PanicRed) PanicRed else TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}

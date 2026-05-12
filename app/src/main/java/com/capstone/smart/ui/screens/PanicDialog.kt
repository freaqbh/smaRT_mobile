package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@Composable
fun PanicDialog(
    viewModel: SmaRTViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSending by remember { mutableStateOf(false) }
    var isSent by remember { mutableStateOf(false) }

    // Auto-trigger panic saat dialog dibuka
    LaunchedEffect(Unit) {
        if (!isSent) {
            isSending = true
            // TODO: Gunakan FusedLocationProviderClient untuk GPS asli
            // Sementara pakai koordinat default (Surabaya)
            viewModel.triggerPanic("-7.289864", "112.751383")
            isSending = false
            isSent = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(top = 220.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PanicOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = PanicOrange,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = PanicOrange,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isSending) "Mengirim SOS..." else "SOS Terkirim!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PanicRed,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isSending)
                            "Sedang mengirim sinyal darurat ke pengurus RT..."
                        else
                            "Lokasi Anda telah dikirim ke Ketua RT dan Satpam. Bantuan sedang menuju ke lokasi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Location card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = PanicRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Lokasi Terdeteksi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary
                                )
                                Text(
                                    text = "-7.289864, 112.751383",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // API result
                    viewModel.panicResult?.let { result ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusGreen,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Cancel button
                    Button(
                        onClick = {
                            viewModel.clearPanicResult()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Batalkan (Salah Tekan)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

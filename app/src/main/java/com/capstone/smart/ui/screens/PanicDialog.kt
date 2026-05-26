package com.capstone.smart.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

@SuppressLint("MissingPermission")
@Composable
fun PanicDialog(
    viewModel: SmaRTViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSending by remember { mutableStateOf(false) }
    var isSent by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<String?>(null) }
    var longitude by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Get location and trigger panic
    LaunchedEffect(hasLocationPermission) {
        if (!isSent) {
            if (!hasLocationPermission) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                isSending = true
                // Get last known location from LocationManager
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = try {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } catch (e: Exception) {
                    null
                }

                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    locationError = null
                    viewModel.triggerPanic(latitude!!, longitude!!)
                } else {
                    // Fallback: use default coordinates if location unavailable
                    latitude = "-7.289864"
                    longitude = "112.751383"
                    locationError = "GPS tidak tersedia, menggunakan lokasi perkiraan."
                    viewModel.triggerPanic(latitude!!, longitude!!)
                }
                isSending = false
                isSent = true
            }
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
                        if (isSending || viewModel.panicLoading) {
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

                    // Title
                    val titleText = when {
                        isSending || viewModel.panicLoading -> "Mengirim SOS..."
                        viewModel.panicError != null -> "Gagal Mengirim"
                        isSent -> "SOS Terkirim!"
                        else -> "SOS"
                    }
                    val titleColor = when {
                        viewModel.panicError != null -> PanicRed
                        else -> PanicRed
                    }

                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.headlineMedium,
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    val descText = when {
                        isSending || viewModel.panicLoading ->
                            "Sedang mengirim sinyal darurat ke pengurus RT..."
                        viewModel.panicError != null ->
                            viewModel.panicError ?: "Terjadi kesalahan."
                        isSent ->
                            "Lokasi Anda telah dikirim ke Ketua RT dan Satpam. Bantuan sedang menuju ke lokasi."
                        else -> ""
                    }

                    Text(
                        text = descText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (viewModel.panicError != null) PanicRed else TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Location card
                    if (latitude != null && longitude != null) {
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
                                        text = if (locationError != null)
                                            "Lokasi Perkiraan"
                                        else
                                            "Lokasi Terdeteksi (GPS)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                    Text(
                                        text = "$latitude, $longitude",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Location warning
                    locationError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusOrange,
                            textAlign = TextAlign.Center
                        )
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

                    // Retry button (if error)
                    if (viewModel.panicError != null && latitude != null && longitude != null) {
                        Button(
                            onClick = {
                                viewModel.triggerPanic(latitude!!, longitude!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PanicOrange,
                                contentColor = Color.White
                            ),
                            enabled = !viewModel.panicLoading
                        ) {
                            Text(
                                text = "Coba Lagi",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

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

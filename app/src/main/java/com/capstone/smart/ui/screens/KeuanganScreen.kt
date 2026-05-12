package com.capstone.smart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.smart.ui.components.SmaRTHeader
import com.capstone.smart.ui.theme.*
import com.capstone.smart.ui.viewmodel.SmaRTViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun KeuanganScreen(
    viewModel: SmaRTViewModel,
    modifier: Modifier = Modifier
) {
    // Load data keuangan saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.loadKeuangan()
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        SmaRTHeader(userName = viewModel.currentUser?.nama)

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Transparansi Keuangan",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.kasLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Teal600, modifier = Modifier.size(32.dp))
            }
        } else {
            // Saldo card — from API kas/monitor
            val saldo = viewModel.kasMonitor?.saldo ?: 0L

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(KeuanganGreenDark, KeuanganGreenLight)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Total Saldo Kas RT",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatter.format(saldo),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (viewModel.kasMonitor?.status_integritas == true)
                                            StatusGreen else PanicRed
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (viewModel.kasMonitor?.status_integritas == true)
                                    "Hashchain: Valid ✓"
                                else
                                    "Hashchain: Tidak Valid ✗",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ringkasan Pemasukan/Pengeluaran
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SummaryCard(
                    label = "Pemasukan",
                    amount = formatter.format(viewModel.kasMonitor?.total_pemasukan ?: 0L),
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Pengeluaran",
                    amount = formatter.format(viewModel.kasMonitor?.total_pengeluaran ?: 0L),
                    color = PanicOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Riwayat Transaksi — dari API kas/history
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.kasHistory.isEmpty()) {
                Text(
                    text = "Belum ada transaksi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "KETERANGAN",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextTertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "JUMLAH",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = DividerGray.copy(alpha = 0.5f))

                        viewModel.kasHistory.takeLast(5).reversed().forEachIndexed { index, tx ->
                            val isIncome = tx.jenis_kas == "PEMASUKAN"
                            val prefix = if (isIncome) "+ " else "- "
                            val amountColor = if (isIncome) StatusGreen else PanicOrange

                            ExpenseRow(
                                description = tx.keterangan,
                                date = tx.created_at?.take(10) ?: "-",
                                amount = "$prefix${formatter.format(tx.nominal)}",
                                amountColor = amountColor
                            )
                            if (index < viewModel.kasHistory.takeLast(5).lastIndex) {
                                HorizontalDivider(
                                    color = DividerGray.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ExpenseRow(
    description: String,
    date: String,
    amount: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.titleSmall,
            color = amountColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

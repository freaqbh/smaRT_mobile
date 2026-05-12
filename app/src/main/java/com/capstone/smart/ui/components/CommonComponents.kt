package com.capstone.smart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.smart.ui.theme.*

@Composable
fun SmaRTHeader(
    userName: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Teal900, Teal700)
                ),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "smaRT",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextOnDark,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "RT 03 / RW 04 Gayungan Surabaya",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextOnDarkSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = TextOnDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            UserProfileCard(userName = userName)
        }
    }
}

@Composable
fun UserProfileCard(
    userName: String? = null,
    modifier: Modifier = Modifier
) {
    val displayName = userName ?: "Warga"
    val initials = displayName.split(" ")
        .take(2)
        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        .ifEmpty { "W" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Teal600.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextOnDark,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextOnDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Warga Tetap",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextOnDarkSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StatusGreen,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Aktif",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SmaRTBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onSosClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("Beranda", Icons.Outlined.Home),
        BottomNavItem("Surat", Icons.Outlined.ChatBubbleOutline),
        BottomNavItem("", Icons.Filled.Warning), // SOS placeholder
        BottomNavItem("Keuangan", Icons.Outlined.AttachMoney),
        BottomNavItem("Akun", Icons.Outlined.PersonOutline)
    )

    Box(modifier = modifier.fillMaxWidth()) {
        // Background bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shadowElevation = 12.dp,
            color = CardWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                items.forEachIndexed { index, item ->
                    if (index == 2) {
                        // SOS FAB spacer
                        Spacer(modifier = Modifier.width(56.dp))
                    } else {
                        val isSelected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onTabSelected(index) }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Teal600 else TextTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Teal600 else TextTertiary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Center SOS FAB
        FloatingActionButton(
            onClick = onSosClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(56.dp),
            shape = CircleShape,
            containerColor = PanicOrange,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(6.dp, 10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "SOS",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun TabToggle(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    activeColor: Color = Teal600,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Button(
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) activeColor else Color.Transparent,
                    contentColor = if (isSelected) Color.White else TextSecondary
                ),
                border = if (!isSelected) {
                    androidx.compose.foundation.BorderStroke(1.dp, DividerGray)
                } else null,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

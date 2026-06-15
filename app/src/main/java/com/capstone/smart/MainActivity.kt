package com.capstone.smart

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capstone.smart.ui.components.SmaRTBottomBar
import com.capstone.smart.ui.screens.*
import com.capstone.smart.ui.theme.SmaRTTheme
import com.capstone.smart.ui.viewmodel.SmaRTViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmaRTTheme {
                SmaRTApp()
            }
        }
    }
}

@Composable
fun SmaRTApp() {
    val viewModel: SmaRTViewModel = viewModel()
    val context = LocalContext.current

    // Notification permission (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPanicDialog by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }

    // Map tab indices to routes
    val tabRoutes = mapOf(
        0 to "beranda",
        1 to "surat",
        3 to "keuangan",
        4 to "akun"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        isLoggedIn = true
                        // Kirim FCM token ke backend setelah login berhasil
                        viewModel.sendFcmToken(context)
                        navController.navigate("beranda") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("beranda") {
                BerandaScreen(
                    viewModel = viewModel,
                    onPanicClick = { showPanicDialog = true },
                    onNavigateToSurat = {
                        selectedTab = 1
                        navController.navigate("surat") {
                            popUpTo("beranda") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToKeuangan = {
                        selectedTab = 3
                        navController.navigate("keuangan") {
                            popUpTo("beranda") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToLapor = {
                        navController.navigate("lapor_warga") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAgenda = {
                        navController.navigate("agenda") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("surat") {
                SuratScreen(viewModel = viewModel)
            }
            composable("keuangan") {
                KeuanganScreen(viewModel = viewModel)
            }
            composable("akun") {
                AkunScreen(
                    viewModel = viewModel,
                    onNavigateToAgenda = {
                        navController.navigate("agenda") {
                            launchSingleTop = true
                        }
                    },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("lapor_warga") {
                LaporWargaScreen(viewModel = viewModel)
            }
            composable("agenda") {
                AgendaScreen(viewModel = viewModel)
            }
        }

        // Bottom Navigation Bar — only shown when logged in
        if (isLoggedIn) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            ) {
                SmaRTBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        val route = tabRoutes[index] ?: return@SmaRTBottomBar
                        navController.navigate(route) {
                            popUpTo("beranda") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSosClick = { showPanicDialog = true }
                )
            }
        }
    }

    // Panic Dialog overlay
    if (showPanicDialog) {
        PanicDialog(
            viewModel = viewModel,
            onDismiss = { showPanicDialog = false }
        )
    }
}
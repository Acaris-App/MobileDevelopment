package com.acaris.features.main.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.acaris.R
import com.acaris.core.navigation.Screen
import com.acaris.core.network.AuthEvent
import com.acaris.core.ui.components.CustomDialog
import com.acaris.features.main.presentation.viewmodel.MainViewModel
import com.acaris.features.profile.ui.screen.EditDataDiriScreen
import com.acaris.features.profile.ui.screen.EditDocumentScreen
import com.acaris.features.profile.ui.screen.ProfileScreen

object AcarisIcons {
    val Schedule: ImageVector = Icons.Default.DateRange
    val Chatbot: ImageVector = Icons.Default.Chat
    val Profile: ImageVector = Icons.Default.PersonOutline
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogoutSuccess: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userRole by viewModel.userRole.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.authEventBus.events.collect { event ->
            if (event is AuthEvent.SessionExpired) {
                onLogoutSuccess()
            }
        }
    }

    // Dialog Logout
    if (showLogoutDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showLogoutDialog = false },
            confirmText = "Keluar",
            dismissText = "Batal",
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onSuccess = onLogoutSuccess)
            },
            onDismiss = { showLogoutDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Keluar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Apakah Anda yakin ingin keluar dari Acaris?",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        )
    }

    if (userRole == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val menus = when (userRole?.lowercase()) {
        "mahasiswa" -> listOf(
            Pair(Screen.HomeMahasiswa.route, Pair(Icons.Default.Home, "Home")),
            Pair(Screen.Schedule.route, Pair(AcarisIcons.Schedule, "Jadwal")),
            Pair(Screen.Chatbot.route, Pair(AcarisIcons.Chatbot, "Chatbot")),
            Pair(Screen.Profile.route, Pair(AcarisIcons.Profile, "Profil"))
        )
        "dosen" -> listOf(
            Pair(Screen.DashboardDosen.route, Pair(Icons.Default.Dashboard, "Dashboard")),
            Pair(Screen.Schedule.route, Pair(AcarisIcons.Schedule, "Jadwal")),
            Pair(Screen.MahasiswaBimbingan.route, Pair(Icons.Default.Group, "Mahasiswa")),
            Pair(Screen.Profile.route, Pair(AcarisIcons.Profile, "Profil"))
        )
        "admin" -> listOf(
            Pair(Screen.DashboardAdmin.route, Pair(Icons.Default.Dashboard, "Dashboard")),
            Pair(Screen.KnowledgeBase.route, Pair(Icons.Default.Book, "Knowledge")),
            Pair(Screen.UserManagement.route, Pair(Icons.Default.ManageAccounts, "Pengguna")),
            Pair(Screen.Profile.route, Pair(AcarisIcons.Profile, "Profil"))
        )
        else -> emptyList()
    }

    val isMainMenu = menus.any { it.first == currentRoute }

    Box(modifier = Modifier.fillMaxSize()) {
        val topPadding = if (isMainMenu) 80.dp else 0.dp
        val bottomPadding = if (isMainMenu) 100.dp else 0.dp

        Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding, top = topPadding)) {
            val startDest = menus.firstOrNull()?.first ?: "home_mahasiswa"

            NavHost(
                navController = bottomNavController,
                startDestination = startDest
            ) {
                composable(Screen.HomeMahasiswa.route) { ScreenPlaceholder("Home Mahasiswa") }
                composable(Screen.DashboardDosen.route) { ScreenPlaceholder("Dashboard Dosen") }
                composable(Screen.DashboardAdmin.route) { ScreenPlaceholder("Dashboard Admin") }
                composable(Screen.Schedule.route) { ScreenPlaceholder("Halaman Jadwal") }
                composable(Screen.Chatbot.route) { ScreenPlaceholder("Halaman Chatbot") }
                composable(Screen.MahasiswaBimbingan.route) { ScreenPlaceholder("Daftar Mahasiswa") }
                composable(Screen.KnowledgeBase.route) { ScreenPlaceholder("Knowledge Base") }
                composable(Screen.UserManagement.route) { ScreenPlaceholder("Manajemen Pengguna") }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onNavigateBack = {
                            bottomNavController.navigate(startDest) {
                                popUpTo(startDest) { inclusive = true }
                            }
                        },
                        onNavigateToEditDataDiri = { bottomNavController.navigate(Screen.EditDataDiri.route) },
                        onNavigateToEditDokumen = { bottomNavController.navigate(Screen.EditDokumen.route) }
                    )
                }

                composable(Screen.EditDataDiri.route) {
                    EditDataDiriScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }

                composable(Screen.EditDokumen.route) {
                    EditDocumentScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
            }
        }

        if (isMainMenu) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val homeRoute = menus.firstOrNull()?.first ?: "home_mahasiswa"
                                bottomNavController.navigate(homeRoute) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Acaris",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ACARIS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                            .border(1.dp, Color.Black, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

        if (isMainMenu && menus.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                    .shadow(elevation = 16.dp, shape = MaterialTheme.shapes.extraLarge)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    menus.forEach { item ->
                        val selected = currentRoute == item.first
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        bottomNavController.navigate(item.first) {
                                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.second.first,
                                contentDescription = item.second.second,
                                tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.second.second,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenPlaceholder(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = Color.Gray)
    }
}
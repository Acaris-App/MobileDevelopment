package com.acaris.features.main.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // 🌟 Import untuk ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.acaris.R
import com.acaris.core.navigation.MainNavHost
import com.acaris.core.navigation.Screen
import com.acaris.core.network.AuthEvent
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomFloatingDropdownMenu // 🌟 Import Custom Component Kapten
import com.acaris.features.main.presentation.model.AdminMenus
import com.acaris.features.main.presentation.model.DosenMenus
import com.acaris.features.main.presentation.model.MahasiswaMenus
import com.acaris.features.main.presentation.viewmodel.MainViewModel

// 🌟 Enum Aksi Profil agar rapi saat dikirim ke CustomFloatingDropdownMenu
enum class MainProfileAction(val label: String, val icon: ImageVector) {
    EDIT("Edit Profil", Icons.Default.Edit),
    CHANGE_PASSWORD("Ganti Password", Icons.Default.Lock),
    LOGOUT("Logout", Icons.AutoMirrored.Filled.Logout)
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

    // State untuk Menu Profil
    var expandedProfileMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.authEventBus.events.collect { event ->
            if (event is AuthEvent.SessionExpired) {
                onLogoutSuccess()
            }
        }
    }

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
                    Text("Keluar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin ingin keluar dari Acaris?", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
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
        "mahasiswa" -> MahasiswaMenus
        "dosen" -> DosenMenus
        "admin" -> AdminMenus
        else -> emptyList()
    }

    val isMainMenu = menus.any { it.route == currentRoute }
    val currentMenuItem = menus.find { it.route == currentRoute }
    val isHomeTab = currentRoute == menus.firstOrNull()?.route
    val isProfileTab = currentRoute == Screen.Profile.route
    val isScheduleTab = currentRoute == Screen.Schedule.route
    val isChatbotTab = currentMenuItem?.title?.contains("Chatbot", ignoreCase = true) == true
    val showMainTopAppBar = isMainMenu && !isChatbotTab

    Box(modifier = Modifier.fillMaxSize()) {
        val topPadding = if (showMainTopAppBar) 64.dp else 0.dp

        Box(modifier = Modifier.fillMaxSize().padding(top = topPadding)) {
            val startDest = menus.firstOrNull()?.route ?: Screen.HomeMahasiswa.route

            MainNavHost(
                navController = bottomNavController,
                startDestination = startDest,
                userRole = userRole
            )
        }

        if (showMainTopAppBar) {
            TopAppBar(
                title = {
                    if (isHomeTab) {
                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() }, indication = null,
                                    onClick = {
                                        val homeRoute = menus.firstOrNull()?.route ?: Screen.HomeMahasiswa.route
                                        bottomNavController.navigate(homeRoute) {
                                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo Acaris", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "ACARIS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = currentMenuItem?.title ?: "Acaris",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                },
                actions = {
                    if (isScheduleTab && userRole?.lowercase() == "mahasiswa") {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            CustomCircularIconButton(
                                icon = Icons.Default.History,
                                contentDescription = "Riwayat Booking",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                    bottomNavController.navigate(Screen.BookingHistory.route) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }

                    // 🌟 MENGGUNAKAN CUSTOM FLOATING DROPDOWN MENU KAPTEN
                    if (isProfileTab) {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            CustomCircularIconButton(
                                icon = Icons.Default.MoreVert,
                                contentDescription = "Opsi Profil",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp),
                                onClick = { expandedProfileMenu = true }
                            )

                            // 🌟 Memanggil Custom Menu yang keren!
                            CustomFloatingDropdownMenu(
                                expanded = expandedProfileMenu,
                                onDismissRequest = { expandedProfileMenu = false },
                                options = MainProfileAction.values().toList(),
                                selectedOption = null, // Tidak ada yang sedang "dipilih" secara aktif
                                optionLabelProvider = { it.label },
                                optionIconProvider = { it.icon },
                                onOptionSelected = { action ->
                                    when (action) {
                                        MainProfileAction.EDIT -> {
                                            bottomNavController.navigate(Screen.EditDataDiri.route)
                                        }
                                        MainProfileAction.CHANGE_PASSWORD -> {
                                            bottomNavController.navigate(Screen.ChangePassword.route)
                                        }
                                        MainProfileAction.LOGOUT -> {
                                            showLogoutDialog = true
                                        }
                                    }
                                }
                            )
                        }
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
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp, horizontal = 24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    menus.forEach { item ->
                        val selected = currentRoute == item.route

                        val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        val itemColor = if (selected) MaterialTheme.colorScheme.primary else unselectedColor

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() }, indication = null,
                                    onClick = {
                                        bottomNavController.navigate(item.route) {
                                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = itemColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = itemColor
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
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}
package com.acaris.features.main.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val MahasiswaMenus = listOf(
    BottomNavItem("home_mahasiswa", "Home", Icons.Default.Home),
    BottomNavItem("jadwal", "Jadwal", Icons.Default.DateRange),
    BottomNavItem("chatbot", "Chatbot", Icons.Default.Chat),
    BottomNavItem("profil", "Profil", Icons.Default.Person)
)

val DosenMenus = listOf(
    BottomNavItem("dashboard_dosen", "Dashboard", Icons.Default.Dashboard),
    BottomNavItem("jadwal", "Jadwal", Icons.Default.DateRange),
    BottomNavItem("daftar_mahasiswa", "Mahasiswa", Icons.Default.Group),
    BottomNavItem("profil", "Profil", Icons.Default.Person)
)

val AdminMenus = listOf(
    BottomNavItem("dashboard_admin", "Dashboard", Icons.Default.Dashboard),
    BottomNavItem("knowledge_base", "Knowledge", Icons.Default.Book),
    BottomNavItem("manajemen_pengguna", "Pengguna", Icons.Default.ManageAccounts),
    BottomNavItem("profil", "Profil", Icons.Default.Person)
)
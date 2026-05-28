package com.acaris.features.main.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
// 🌟 IMPOR KELAS SCREEN MILIK KAPTEN
import com.acaris.core.navigation.Screen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val MahasiswaMenus = listOf(
    BottomNavItem(Screen.HomeMahasiswa.route, "Home", Icons.Default.Home),
    BottomNavItem(Screen.Schedule.route, "Jadwal", Icons.Default.DateRange),
    BottomNavItem(Screen.Chatbot.route, "Chatbot", Icons.Default.Chat),
    BottomNavItem(Screen.Profile.route, "Profil", Icons.Default.Person)
)

val DosenMenus = listOf(
    BottomNavItem(Screen.DashboardDosen.route, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(Screen.Schedule.route, "Jadwal", Icons.Default.DateRange),
    BottomNavItem(Screen.MahasiswaBimbingan.route, "Mahasiswa", Icons.Default.Group),
    BottomNavItem(Screen.Profile.route, "Profil", Icons.Default.Person)
)

val AdminMenus = listOf(
    BottomNavItem(Screen.DashboardAdmin.route, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(Screen.KnowledgeBase.route, "Knowledge", Icons.Default.Book),
    BottomNavItem(Screen.UserManagement.route, "Pengguna", Icons.Default.ManageAccounts),
    BottomNavItem(Screen.Profile.route, "Profil", Icons.Default.Person)
)
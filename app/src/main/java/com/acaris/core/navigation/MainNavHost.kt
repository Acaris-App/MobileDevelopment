package com.acaris.core.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.acaris.core.ui.components.CustomDialog
import com.acaris.features.auth.ui.screen.ChangePasswordScreen
import com.acaris.features.dashboard.ui.screen.DosenDashboardScreen
import com.acaris.features.main.ui.screen.ScreenPlaceholder
import com.acaris.features.profile.ui.screen.EditDataDiriScreen
import com.acaris.features.profile.ui.screen.EditDocumentScreen
import com.acaris.features.profile.ui.screen.ProfileScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
    userRole: String?
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.HomeMahasiswa.route) { ScreenPlaceholder("Home Mahasiswa") }

        composable(Screen.DashboardDosen.route) {
            val profileViewModel: com.acaris.features.profile.presentation.viewmodel.ProfileViewModel = hiltViewModel()
            val profileState by profileViewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                profileViewModel.loadProfile()
            }

            if (profileState.errorMessage != null) {
                CustomDialog(
                    showDialog = true,
                    onDismissRequest = { profileViewModel.clearMessages() },
                    confirmText = "Tutup",
                    onConfirm = { profileViewModel.clearMessages() },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gagal Memuat Data", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(profileState.errorMessage ?: "", textAlign = TextAlign.Center, color = Color.Gray)
                        }
                    }
                )
            }

            DosenDashboardScreen(
                dosenName = profileState.userProfile?.name ?: "Memuat...",
                kodeKelas = profileState.userProfile?.kodeKelas
            )
        }

        composable(Screen.DashboardAdmin.route) { ScreenPlaceholder("Dashboard Admin") }

        composable(Screen.Schedule.route) {
            if (userRole?.lowercase() == "dosen") {
                com.acaris.features.schedule.ui.screen.DosenScheduleScreen()
            } else {
                com.acaris.features.schedule.ui.screen.MahasiswaScheduleScreen(
                    onNavigateToHistory = {
                        navController.navigate(Screen.BookingHistory.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(Screen.BookingHistory.route) {
            com.acaris.features.schedule.ui.screen.BookingHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Chatbot.route) { ScreenPlaceholder("Halaman Chatbot") }

        composable(Screen.MahasiswaBimbingan.route) {
            com.acaris.features.monitoring_mahasiswa.ui.screen.MonitoringListScreen(
                onNavigateToDetail = { mahasiswaId ->
                    navController.navigate(Screen.DetailMahasiswa.createRoute(mahasiswaId))
                }
            )
        }

        composable(Screen.DetailMahasiswa.route) { backStackEntry ->
            val mahasiswaId = backStackEntry.arguments?.getString("mahasiswaId") ?: ""

            com.acaris.features.monitoring_mahasiswa.ui.screen.MonitoringDetailScreen(
                mahasiswaId = mahasiswaId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistoryBimbingan = { id ->
                    navController.navigate(Screen.HistoryBimbinganMahasiswa.createRoute(id))
                },
                onNavigateToHistoryChatbot = { id ->
                    // (Tunggu fiturnya kelar nanti)
                }
            )
        }

        composable(Screen.HistoryBimbinganMahasiswa.route) { backStackEntry ->
            val mahasiswaId = backStackEntry.arguments?.getString("mahasiswaId") ?: ""

            com.acaris.features.monitoring_mahasiswa.ui.screen.MonitoringHistoryScreen(
                mahasiswaId = mahasiswaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.KnowledgeBase.route) {
            com.acaris.features.knowledge_base.ui.screen.KnowledgeBaseScreen()
        }

        composable(Screen.UserManagement.route) {
            com.acaris.features.user_management.ui.screen.UserManagementScreen(
                onNavigateToEdit = { userId ->
                    navController.navigate(Screen.EditUser.createRoute(userId))
                },
                onNavigateToAddAdmin = {
                    navController.navigate(Screen.AddAdmin.route)
                },
                onNavigateToDetail = { userId ->
                    navController.navigate(Screen.UserDetail.createRoute(userId))
                }
            )
        }

        composable(Screen.UserDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            com.acaris.features.user_management.ui.screen.UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddAdmin.route) {
            com.acaris.features.user_management.ui.screen.AddAdminScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.EditUser.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            com.acaris.features.user_management.ui.screen.EditUserScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.navigate(startDestination) {
                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                onNavigateToEditDataDiri = { navController.navigate(Screen.EditDataDiri.route) },
                onNavigateToEditDokumen = { navController.navigate(Screen.EditDokumen.route) },
                onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) }
            )
        }

        composable(Screen.EditDataDiri.route) {
            EditDataDiriScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.EditDokumen.route) {
            EditDocumentScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
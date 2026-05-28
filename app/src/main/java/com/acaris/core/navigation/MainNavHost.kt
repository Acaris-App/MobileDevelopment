package com.acaris.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.acaris.features.auth.ui.screen.ChangePasswordScreen
import com.acaris.features.dashboard.ui.screen.AdminDashboardScreen
import com.acaris.features.chatbot.ui.screen.ChatbotScreen
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
        composable(Screen.HomeMahasiswa.route) {
            val context = androidx.compose.ui.platform.LocalContext.current

            com.acaris.features.dashboard.ui.screen.MahasiswaDashboardScreen(
                onNavigateToSchedule = { targetDate ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("selected_date", targetDate)
                    navController.navigate(Screen.Schedule.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToHistoryBimbingan = {
                    navController.navigate(Screen.BookingHistory.route) { launchSingleTop = true }
                },
                onNavigateToHistoryChatbot = {
                    android.widget.Toast.makeText(context, "Fitur Riwayat Chatbot segera hadir!", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable(Screen.DashboardDosen.route) {
            com.acaris.features.dashboard.ui.screen.DosenDashboardScreen(
                onNavigateToSchedule = { targetDate ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("selected_date", targetDate)
                    navController.navigate(Screen.Schedule.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToMonitoring = {
                    navController.navigate(Screen.MahasiswaBimbingan.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.DashboardAdmin.route) {
            AdminDashboardScreen()
        }

        composable(Screen.Schedule.route) {
            val previousEntry = navController.previousBackStackEntry
            val passedDate = previousEntry?.savedStateHandle?.get<String>("selected_date")

            previousEntry?.savedStateHandle?.remove<String>("selected_date")

            if (userRole?.lowercase() == "dosen") {
                com.acaris.features.schedule.ui.screen.DosenScheduleScreen(
                    initialSelectedDate = passedDate
                )
            } else {
                com.acaris.features.schedule.ui.screen.MahasiswaScheduleScreen(
                    initialSelectedDate = passedDate,
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

        // 🌟 REVISI UTAMA: Arahkan rute ke ChatbotScreen asli buatan kita
        composable(Screen.Chatbot.route) {
            ChatbotScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

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
                onNavigateToHistoryChatbot = { chatbotId ->
                    // (Tunggu fiturnya kelar nanti)
                }
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
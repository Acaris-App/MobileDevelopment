package com.acaris.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.acaris.features.auth.ui.screen.ChangePasswordScreen
import com.acaris.features.dashboard.ui.screen.AdminDashboardScreen
import com.acaris.features.chatbot.ui.screen.ChatbotScreen
import com.acaris.features.profile.ui.screen.EditDataDiriScreen
import com.acaris.features.profile.ui.screen.ProfileScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
    userRole: String?
) {
    val chatbotHistoryViewModel: com.acaris.features.chatbot.presentation.viewmodel.ChatbotHistoryViewModel = hiltViewModel()

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
                    navController.navigate(Screen.ChatbotHistory.route) { launchSingleTop = true }
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

        composable(Screen.Chatbot.route) {
            ChatbotScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = {
                    navController.navigate(Screen.ChatbotHistory.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.ChatbotHistory.route) {
            com.acaris.features.chatbot.ui.screen.ChatbotHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { sessionId ->
                    navController.navigate(Screen.ChatbotHistoryDetail.createRoute(sessionId))
                },
                viewModel = chatbotHistoryViewModel
            )
        }

        composable(Screen.ChatbotHistoryDetail.route) {
            com.acaris.features.chatbot.ui.screen.ChatbotHistoryDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = chatbotHistoryViewModel
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
                onNavigateToChatbotDetail = { mId, sId ->
                    navController.navigate(Screen.MahasiswaChatbotDetail.createRoute(mId, sId))
                }
            )
        }

        composable(Screen.MahasiswaChatbotDetail.route) { backStackEntry ->
            val mahasiswaId = backStackEntry.arguments?.getString("mahasiswaId") ?: ""
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.DetailMahasiswa.route)
            }
            val sharedViewModel: com.acaris.features.monitoring_mahasiswa.presentation.viewmodel.MonitoringViewModel = hiltViewModel(parentEntry)

            com.acaris.features.monitoring_mahasiswa.ui.screen.MahasiswaChatbotDetailScreen(
                mahasiswaId = mahasiswaId,
                sessionId = sessionId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = sharedViewModel
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChatbotDetail = { uId, sId ->
                    navController.navigate(Screen.AdminMahasiswaChatbotDetail.createRoute(uId, sId))
                }
            )
        }

        composable(Screen.AdminMahasiswaChatbotDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.UserDetail.route)
            }
            val sharedViewModel: com.acaris.features.user_management.presentation.viewmodel.UserDetailViewModel = hiltViewModel(parentEntry)

            com.acaris.features.user_management.ui.screen.AdminChatbotDetailScreen(
                userId = userId,
                sessionId = sessionId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = sharedViewModel
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
                onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) }
            )
        }

        composable(Screen.EditDataDiri.route) {
            EditDataDiriScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
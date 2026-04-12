package com.acaris.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.acaris.core.navigation.Screen
import com.acaris.features.main.ui.screen.MainScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(route = Screen.MainApp.route) {
        MainScreen(
            onLogoutSuccess = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true } // Hapus semua riwayat saat logout
                }
            }
        )
    }
}
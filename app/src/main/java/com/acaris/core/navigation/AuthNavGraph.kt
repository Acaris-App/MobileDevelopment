package com.acaris.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.acaris.core.navigation.Screen
import com.acaris.features.auth.ui.screen.ForgotPasswordScreen
import com.acaris.features.auth.ui.screen.LoginScreen
import com.acaris.features.auth.ui.screen.RegisterScreen
import com.acaris.features.onboarding.ui.screen.WelcomeScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {

    composable(route = Screen.Welcome.route) {
        WelcomeScreen(
            onLoginClick = { navController.navigate(Screen.Login.route) },
            onRegisterClick = { role -> navController.navigate("${Screen.Register.route}/$role") }
        )
    }

    composable(route = Screen.Login.route) {
        LoginScreen(
            onBackClick = { navController.popBackStack() },
            onLoginSuccess = { _ ->
                navController.navigate(Screen.MainApp.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            },
            onNavigateToRegister = { role ->
                navController.navigate("${Screen.Register.route}/$role")
            },
            onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
        )
    }

    composable(route = "${Screen.Register.route}/{role}") { backStackEntry ->
        val role = backStackEntry.arguments?.getString("role") ?: "mahasiswa"
        RegisterScreen(
            role = role,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = false }
                }
            },
            onRegisterSuccess = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = false }
                }
            }
        )
    }

    composable(route = Screen.ForgotPassword.route) {
        ForgotPasswordScreen(
            onNavigateBack = { navController.popBackStack() },
            onResetSuccess = { navController.popBackStack() }
        )
    }
}
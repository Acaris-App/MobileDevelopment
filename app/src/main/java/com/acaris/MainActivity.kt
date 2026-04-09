package com.acaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acaris.core.navigation.Screen
import com.acaris.core.network.datastore.AuthPreferences
import com.acaris.core.ui.theme.AcarisTheme
import com.acaris.features.auth.ui.screen.ForgotPasswordScreen
import com.acaris.features.auth.ui.screen.LoginScreen
import com.acaris.features.auth.ui.screen.RegisterScreen
import com.acaris.features.onboarding.ui.screen.WelcomeScreen
import com.acaris.features.main.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcarisTheme {
                // 🌟 State penahan awal (Splash Screen kilat)
                var startDestination by remember { mutableStateOf<String?>(null) }

                // 🌟 Baca DataStore sekali saja saat aplikasi dibuka
                LaunchedEffect(Unit) {
                    authPreferences.getAuthToken().collect { token ->
                        if (startDestination == null) {
                            startDestination = if (token.isNullOrBlank()) Screen.Welcome.route else Screen.MainApp.route
                        }
                    }
                }

                // 🌟 TAHAN APLIKASI JIKA DATASTORE BELUM SELESAI MEMBACA
                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = startDestination!!,
                            modifier = Modifier.padding(innerPadding)
                        ) {

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
                                        navController.navigate("${Screen.Register.route}/$role") {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
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

                            composable(route = Screen.MainApp.route) {
                                MainScreen(
                                    onLogoutSuccess = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
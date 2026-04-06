package com.acaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acaris.core.navigation.Screen
import com.acaris.core.ui.theme.AcarisTheme
import com.acaris.features.auth.ui.screen.LoginScreen
import com.acaris.features.auth.ui.screen.RegisterScreen
import com.acaris.features.onboarding.ui.screen.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcarisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Welcome.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ==========================================
                        // RUTE ONBOARDING / WELCOME
                        // ==========================================
                        composable(route = Screen.Welcome.route) {
                            WelcomeScreen(
                                onLoginClick = {
                                    navController.navigate(Screen.Login.route)
                                },
                                onRegisterClick = { role ->
                                    navController.navigate("${Screen.Register.route}/$role")
                                }
                            )
                        }

                        // ==========================================
                        // RUTE LOGIN
                        // ==========================================
                        composable(route = Screen.Login.route) {
                            LoginScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onLoginSuccess = { role ->
                                    val targetRoute = when (role) {
                                        "mahasiswa" -> Screen.HomeMahasiswa.route
                                        "dosen" -> Screen.DashboardDosen.route
                                        "admin" -> Screen.DashboardAdmin.route
                                        else -> Screen.Welcome.route
                                    }

                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { role ->
                                    navController.navigate("${Screen.Register.route}/$role") {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(Screen.ForgotPassword.route)
                                }
                            )
                        }

                        // ==========================================
                        // RUTE REGISTER (Menerima Parameter Role)
                        // ==========================================
                        composable(route = "${Screen.Register.route}/{role}") { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: "mahasiswa"

                            RegisterScreen(
                                role = role,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = false }
                                    }
                                },
                                onRegisterSuccess = {
                                    val targetRoute = when (role) {
                                        "mahasiswa" -> Screen.HomeMahasiswa.route
                                        "dosen" -> Screen.DashboardDosen.route
                                        else -> Screen.Welcome.route
                                    }

                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ==========================================
                        // RUTE DASHBOARD / HOME (DUMMY SEMENTARA)
                        // ==========================================
                        composable(route = Screen.HomeMahasiswa.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Home Mahasiswa")
                            }
                        }

                        composable(route = Screen.DashboardDosen.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Dashboard Dosen")
                            }
                        }

                        composable(route = Screen.DashboardAdmin.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Dashboard Admin")
                            }
                        }

                        // Rute Kosong Lainnya
                        composable(route = Screen.ForgotPassword.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Lupa Password")
                            }
                        }
                    }
                }
            }
        }
    }
}
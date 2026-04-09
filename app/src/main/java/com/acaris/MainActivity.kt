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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.acaris.core.navigation.Screen
import com.acaris.core.ui.theme.AcarisTheme
import com.acaris.features.auth.ui.screen.ForgotPasswordScreen
import com.acaris.features.auth.ui.screen.LoginScreen
import com.acaris.features.auth.ui.screen.RegisterScreen
import com.acaris.features.onboarding.ui.screen.WelcomeScreen
import com.acaris.features.dashboard.ui.screen.DosenDashboardScreen // 🌟 Import Layar Dashboard Baru
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
                                // 🌟 Tangkap objek UserUiModel seutuhnya
                                onLoginSuccess = { user ->
                                    if (user.role?.lowercase() == "dosen") {
                                        // Cegah Crash jika kodeKelas dari server null/kosong
                                        val safeKode = if (user.kodeKelas.isNullOrBlank()) "BELUM_ADA" else user.kodeKelas
                                        val safeName = if (user.name.isNullOrBlank()) "Dosen" else user.name

                                        // Lempar ke Dashboard Sementara dengan parameter
                                        navController.navigate("dashboard_dosen_temp/$safeName/$safeKode") {
                                            popUpTo(Screen.Welcome.route) { inclusive = true }
                                        }
                                    } else if (user.role?.lowercase() == "mahasiswa") {
                                        navController.navigate(Screen.HomeMahasiswa.route) {
                                            popUpTo(Screen.Welcome.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(Screen.DashboardAdmin.route) {
                                            popUpTo(Screen.Welcome.route) { inclusive = true }
                                        }
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
                                    // 🌟 Setelah berhasil mendaftar, user diarahkan ke Login (agar bisa dapat Kode Kelas)
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = false }
                                    }
                                }
                            )
                        }

                        // ==========================================
                        // 🌟 RUTE DASHBOARD DOSEN (SEMENTARA)
                        // ==========================================
                        composable(
                            route = "dashboard_dosen_temp/{nama}/{kode}",
                            arguments = listOf(
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("kode") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val nama = backStackEntry.arguments?.getString("nama") ?: "Dosen"
                            val kode = backStackEntry.arguments?.getString("kode")?.let {
                                if (it == "BELUM_ADA") null else it
                            }

                            // Panggil Komponen Layar yang baru kita buat
                            DosenDashboardScreen(dosenName = nama, kodeKelas = kode)
                        }

                        // ==========================================
                        // RUTE HOME LAINNYA
                        // ==========================================
                        composable(route = Screen.HomeMahasiswa.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Home Mahasiswa")
                            }
                        }

                        composable(route = Screen.DashboardAdmin.route) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Halaman Dashboard Admin")
                            }
                        }

                        composable(route = Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onResetSuccess = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
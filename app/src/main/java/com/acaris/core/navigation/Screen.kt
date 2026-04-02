package com.acaris.core.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")

    // Auth
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")

    // Main Screens
    object Dashboard : Screen("dashboard_screen")
    object Profile : Screen("profile_screen")

    // Fitur Dosen
    object MahasiswaBimbingan : Screen("mahasiswa_bimbingan_screen")
    object Schedule : Screen("schedule_screen")

    // Fitur Admin
    object UserManagement : Screen("user_management_screen")

    // Contoh rute dengan Parameter (ID Mahasiswa)
    object DetailMahasiswa : Screen("detail_mahasiswa_screen/{mahasiswaId}") {
        fun createRoute(mahasiswaId: String) = "detail_mahasiswa_screen/$mahasiswaId"
    }
}
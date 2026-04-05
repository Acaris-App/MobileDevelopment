package com.acaris.core.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")

    // Auth
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ForgotPassword : Screen("forgot_password_screen")

    // Main Screens
    object HomeMahasiswa : Screen("home_mahasiswa_screen")
    object DashboardDosen : Screen("dashboard_dosen_screen")
    object DashboardAdmin : Screen("dashboard_admin_screen")
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
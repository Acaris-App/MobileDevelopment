// File: core/navigation/Screen.kt
package com.acaris.core.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")

    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ForgotPassword : Screen("forgot_password_screen")

    object MainApp : Screen("main_app_screen")

    object HomeMahasiswa : Screen("home_mahasiswa_screen")
    object DashboardDosen : Screen("dashboard_dosen_screen")
    object DashboardAdmin : Screen("dashboard_admin_screen")
    object Profile : Screen("profile_screen")
    object Schedule : Screen("schedule_screen")

    object BookingHistory : Screen("booking_history_screen")

    object Chatbot : Screen("chatbot_screen")
    object KnowledgeBase : Screen("knowledge_base_screen")
    object MahasiswaBimbingan : Screen("mahasiswa_bimbingan_screen")
    object UserManagement : Screen("user_management_screen")

    object EditDataDiri : Screen("edit_data_diri_screen")
    object EditDokumen : Screen("edit_dokumen_screen")
    object ChangePassword : Screen("change_password_screen")

    object DetailMahasiswa : Screen("detail_mahasiswa_screen/{mahasiswaId}") {
        fun createRoute(mahasiswaId: String) = "detail_mahasiswa_screen/$mahasiswaId"
    }

    object HistoryBimbinganMahasiswa : Screen("history_bimbingan_mahasiswa_screen/{mahasiswaId}") {
        fun createRoute(mahasiswaId: String) = "history_bimbingan_mahasiswa_screen/$mahasiswaId"
    }

    object AddAdmin : Screen("add_admin_screen")

    object EditUser : Screen("edit_user_screen/{userId}") {
        fun createRoute(userId: String) = "edit_user_screen/$userId"
    }

    object UserDetail : Screen("user_detail_screen/{userId}") {
        fun createRoute(userId: String) = "user_detail_screen/$userId"
    }
}
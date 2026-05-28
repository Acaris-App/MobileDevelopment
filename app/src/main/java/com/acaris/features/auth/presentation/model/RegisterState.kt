package com.acaris.features.auth.presentation.model

enum class RegisterStep {
    LOADING_INIT,
    INPUT_KODE_KELAS,
    INPUT_DATA_DIRI,
    INPUT_OTP,
    UPLOAD_DOKUMEN,
    SUCCESS_REGISTER
}

data class RegisterState(
    val currentStep: RegisterStep = RegisterStep.LOADING_INIT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserUiModel? = null, // 🌟 Ganti di sini
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val npm: String = "",
    val nip: String = "",
    val angkatan: String = "",
    val semester: String = "",
    val ipk: String = ""
)
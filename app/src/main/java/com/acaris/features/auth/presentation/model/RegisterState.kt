package com.acaris.features.auth.presentation.model

enum class RegisterStep {
    LOADING_INIT, // 🌟 TAMBAHAN BARU
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
    val user: UserPresentationModel? = null
)
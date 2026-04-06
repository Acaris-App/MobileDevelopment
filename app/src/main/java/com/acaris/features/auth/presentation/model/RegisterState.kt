package com.acaris.features.auth.presentation.model

enum class RegisterStep {
    INPUT_KODE_KELAS, // Langkah 1 (Khusus Mahasiswa)
    INPUT_DATA_DIRI,  // Langkah 2 (Mahasiswa & Dosen)
    INPUT_OTP,        // Langkah 3 (Mahasiswa & Dosen)
    UPLOAD_DOKUMEN,   // Langkah 4 (Khusus Mahasiswa)
    SUCCESS_REGISTER  // Selesai
}

data class RegisterState(
    val currentStep: RegisterStep = RegisterStep.INPUT_DATA_DIRI,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserPresentationModel? = null
)
package com.acaris.features.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class ValidateKodeKelasRequest(
    @SerializedName("kode_kelas")
    val kodeKelas: String
)

data class VerifyOtpRequest(
    val email: String,
    @SerializedName("otp_code")
    val otpCode: String
)
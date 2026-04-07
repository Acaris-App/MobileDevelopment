package com.acaris.features.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class ValidateKodeKelasRequest(
    @SerializedName("kode_kelas")
    val kodeKelas: String
)

data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("code")
    val otpCode: String
)

data class ResendOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("type")
    val type: String = "register"
)
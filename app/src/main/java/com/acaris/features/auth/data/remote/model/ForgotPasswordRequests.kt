package com.acaris.features.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class VerifyResetOtpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("new_password")
    val newPassword: String
)
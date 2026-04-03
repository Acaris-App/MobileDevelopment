package com.acaris.features.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AuthDataModel? = null
)

data class AuthDataModel(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserRemoteModel
)

data class UserRemoteModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("role")
    val role: String
)
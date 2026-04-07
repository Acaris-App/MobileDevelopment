package com.acaris.features.auth.domain.model

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val token: String,
    val kodeKelas: String? = null
)
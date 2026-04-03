package com.acaris.features.auth.presentation.model

// Model khusus untuk layer presentasi (tidak mengandung token rahasia)
data class UserPresentationModel(
    val email: String,
    val name: String,
    val role: String
)
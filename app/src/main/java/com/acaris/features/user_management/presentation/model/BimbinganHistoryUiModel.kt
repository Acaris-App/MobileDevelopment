package com.acaris.features.user_management.presentation.model

data class BimbinganHistoryUiModel(
    val id: String,
    val date: String,
    val time: String,
    val agenda: String,
    val status: String,
    val keteranganDosen: String
)
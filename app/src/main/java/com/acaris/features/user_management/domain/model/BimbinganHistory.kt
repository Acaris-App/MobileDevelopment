package com.acaris.features.user_management.domain.model

data class BimbinganHistory(
    val id: String,
    val date: String,
    val time: String,
    val agenda: String,
    val status: String,
    val keteranganDosen: String
)
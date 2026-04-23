package com.acaris.features.schedule.domain.model

data class Schedule(
    val id: String,
    val dosenId: String,
    val dosenName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val quota: Int,
    val remainingQuota: Int,
    val status: String,
    val keterangan: String
)
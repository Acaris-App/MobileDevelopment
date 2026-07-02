package com.acaris.features.schedule.presentation.model

data class ScheduleUiModel(
    val id: String,
    val title: String,
    val date: String = "",
    val rawDate: String = "",
    val time: String,
    val quotaInfo: String,
    val status: String,
    val keterangan: String,
    val rawStartTime: String = "",
    val rawEndTime: String = "",
    val rawQuota: Int = 0,
    val isFull: Boolean = false,
    val bookedStudents: List<StudentBookingUiModel> = emptyList(),
    val dosenName: String = "",
    val isBookedByMe: Boolean = false,
    val bookingId: String? = null,
    val myAgenda: String? = null,
    val isSelesai: Boolean = false
)

data class StudentBookingUiModel(
    val id: String,
    val nama: String,
    val npm: String,
    val keterangan: String
)
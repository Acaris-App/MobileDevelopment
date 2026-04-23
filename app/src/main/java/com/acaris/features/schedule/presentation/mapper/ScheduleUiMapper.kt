package com.acaris.features.schedule.presentation.mapper

import com.acaris.features.schedule.domain.model.Schedule
import com.acaris.features.schedule.presentation.model.ScheduleUiModel

fun Schedule.toPresentation(): ScheduleUiModel {
    val isScheduleFull = this.remainingQuota <= 0

    return ScheduleUiModel(
        id = this.id,
        title = "Bimbingan Akademik",
        time = "${this.startTime} - ${this.endTime}",
        quotaInfo = if (isScheduleFull) "Penuh (${this.quota}/${this.quota})" else "Sisa Kuota: ${this.remainingQuota}/${this.quota}",
        status = this.status,
        keterangan = this.keterangan.ifBlank { "Tidak ada keterangan" },
        rawStartTime = this.startTime,
        rawEndTime = this.endTime,
        rawQuota = this.quota,
        isFull = isScheduleFull
    )
}
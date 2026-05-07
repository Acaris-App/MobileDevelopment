package com.acaris.features.user_management.presentation.mapper

import com.acaris.core.utils.DateUtils
import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.model.MahasiswaDocument
import com.acaris.features.user_management.presentation.model.BimbinganHistoryUiModel
import com.acaris.features.user_management.presentation.model.MahasiswaDocumentUiModel

fun MahasiswaDocument.toUiModel(): MahasiswaDocumentUiModel {
    return MahasiswaDocumentUiModel(
        id = this.id,
        documentType = this.documentType,
        semester = this.semester,
        filePath = this.filePath,
        uploadedAt = DateUtils.formatIsoToIndo(this.uploadedAt)
    )
}

fun BimbinganHistory.toUiModel(): BimbinganHistoryUiModel {
    return BimbinganHistoryUiModel(
        id = this.id,
        date = this.date,
        time = this.time,
        agenda = this.agenda,
        status = this.status,
        keteranganDosen = this.keteranganDosen
    )
}
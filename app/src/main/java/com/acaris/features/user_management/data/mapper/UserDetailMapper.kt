package com.acaris.features.user_management.data.mapper

import com.acaris.features.user_management.data.remote.model.BimbinganHistoryResponse
import com.acaris.features.user_management.data.remote.model.MahasiswaDocumentResponse
import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.model.MahasiswaDocument

fun MahasiswaDocumentResponse.toDomain(): MahasiswaDocument {
    return MahasiswaDocument(
        id = this.id ?: "",
        documentType = this.documentType ?: "-",
        semester = this.semester ?: 0,
        filePath = this.filePath ?: "",
        uploadedAt = this.uploadedAt ?: "-"
    )
}

fun BimbinganHistoryResponse.toDomain(): BimbinganHistory {
    return BimbinganHistory(
        id = this.id ?: "",
        date = this.date ?: "-",
        time = this.time ?: "-",
        agenda = this.agenda ?: "Tanpa Agenda",
        status = this.status ?: "unknown",
        keteranganDosen = this.keteranganDosen ?: "-"
    )
}
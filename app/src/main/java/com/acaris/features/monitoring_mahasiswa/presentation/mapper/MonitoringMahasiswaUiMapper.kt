package com.acaris.features.monitoring_mahasiswa.presentation.mapper

import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.DokumenBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.presentation.model.DetailMahasiswaUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.DokumenBimbinganUiModel
import com.acaris.features.monitoring_mahasiswa.presentation.model.MahasiswaBimbinganUiModel

fun MahasiswaBimbingan.toUiModel(): MahasiswaBimbinganUiModel {
    return MahasiswaBimbinganUiModel(
        id = this.id,
        name = this.name,
        npm = this.npm,
        profilePictureUrl = this.profilePictureUrl.orEmpty(),
        infoAkademik = "Angkatan ${this.angkatan ?: "-"} | Semester ${this.currentSemester ?: "-"}"
    )
}

fun DokumenBimbingan.toUiModel(): DokumenBimbinganUiModel {
    val semesterText = this.semester?.let { "Semester $it" } ?: ""
    return DokumenBimbinganUiModel(
        id = this.id,
        title = "${this.type.uppercase()} $semesterText".trim(),
        fileUrl = this.fileUrl,
        uploadedAt = this.uploadedAt
    )
}

fun DetailMahasiswa.toUiModel(): DetailMahasiswaUiModel {
    return DetailMahasiswaUiModel(
        id = this.id,
        name = this.name,
        npm = this.npm,
        email = this.email.ifBlank { "Email belum diatur" },
        profilePictureUrl = this.profilePictureUrl.orEmpty(),
        ipk = this.ipk ?: "0.00",
        angkatan = this.angkatan?.toString() ?: "-",
        semester = this.currentSemester?.toString() ?: "-",
        kodeKelas = this.kodeKelas ?: "Belum masuk kelas",
        dokumen = this.dokumen.map { it.toUiModel() }
    )
}
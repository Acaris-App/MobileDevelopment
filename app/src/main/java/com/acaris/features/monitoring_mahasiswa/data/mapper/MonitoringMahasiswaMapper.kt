package com.acaris.features.monitoring_mahasiswa.data.mapper

import com.acaris.features.monitoring_mahasiswa.data.remote.model.DetailMahasiswaResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.DokumenBimbinganResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.MahasiswaBimbinganResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.RiwayatBimbinganResponse
import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.model.DokumenBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan

fun MahasiswaBimbinganResponse.toDomain(): MahasiswaBimbingan {
    return MahasiswaBimbingan(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        npm = this.npmNip.orEmpty(),
        profilePictureUrl = this.profilePicture,
        angkatan = this.angkatan,
        currentSemester = this.currentSemester
    )
}

fun DokumenBimbinganResponse.toDomain(): DokumenBimbingan {
    return DokumenBimbingan(
        id = this.id.orEmpty(),
        type = this.documentType.orEmpty(),
        semester = this.semester,
        fileUrl = this.filePath.orEmpty(),
        uploadedAt = this.uploadedAt.orEmpty()
    )
}

fun DetailMahasiswaResponse.toDomain(): DetailMahasiswa {
    return DetailMahasiswa(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        npm = this.npmNip.orEmpty(),
        email = this.email.orEmpty(),
        profilePictureUrl = this.profilePicture,
        angkatan = this.angkatan,
        ipk = this.ipk,
        currentSemester = this.currentSemester,
        kodeKelas = this.kodeKelas,
        dokumen = this.documents?.map { it.toDomain() } ?: emptyList()
    )
}

fun RiwayatBimbinganResponse.toDomain(): RiwayatBimbingan {
    return RiwayatBimbingan(
        id = this.id.orEmpty(),
        date = this.date?.substringBefore("T").orEmpty(),
        time = this.time.orEmpty(),
        agenda = this.agenda.orEmpty(),
        status = this.status.orEmpty(),
        keteranganDosen = this.keteranganDosen.orEmpty()
    )
}
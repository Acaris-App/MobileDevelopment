package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import javax.inject.Inject

class GetDaftarMahasiswaUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    suspend operator fun invoke(): Result<List<MahasiswaBimbingan>> {
        return repository.getDaftarMahasiswa()
    }
}
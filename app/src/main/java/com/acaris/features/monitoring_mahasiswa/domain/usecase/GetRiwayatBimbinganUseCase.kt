package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.monitoring_mahasiswa.domain.model.RiwayatBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import javax.inject.Inject

class GetRiwayatBimbinganUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    suspend operator fun invoke(mahasiswaId: String): Result<List<RiwayatBimbingan>> {
        return repository.getRiwayatBimbingan(mahasiswaId)
    }
}
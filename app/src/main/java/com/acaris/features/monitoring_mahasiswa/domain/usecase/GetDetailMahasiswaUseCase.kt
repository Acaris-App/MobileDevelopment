package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import javax.inject.Inject

class GetDetailMahasiswaUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    suspend operator fun invoke(mahasiswaId: String): Result<DetailMahasiswa> {
        return repository.getDetailMahasiswa(mahasiswaId)
    }
}
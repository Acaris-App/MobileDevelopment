package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.monitoring_mahasiswa.domain.model.DetailMahasiswa
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDetailMahasiswaUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    operator fun invoke(mahasiswaId: String): Flow<DetailMahasiswa> {
        return repository.getDetailMahasiswa(mahasiswaId)
    }
}
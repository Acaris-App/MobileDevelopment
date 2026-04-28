package com.acaris.features.monitoring_mahasiswa.domain.usecase

import com.acaris.features.monitoring_mahasiswa.domain.model.MahasiswaBimbingan
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDaftarMahasiswaUseCase @Inject constructor(
    private val repository: MonitoringMahasiswaRepository
) {
    operator fun invoke(): Flow<List<MahasiswaBimbingan>> {
        return repository.getDaftarMahasiswa()
    }
}
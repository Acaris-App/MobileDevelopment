package com.acaris.features.monitoring_mahasiswa.di

import com.acaris.features.monitoring_mahasiswa.data.remote.datasource.MonitoringMahasiswaApiService
import com.acaris.features.monitoring_mahasiswa.data.repository.MonitoringMahasiswaRepositoryImpl
import com.acaris.features.monitoring_mahasiswa.domain.repository.MonitoringMahasiswaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonitoringMahasiswaModule {

    @Provides
    @Singleton
    fun provideMonitoringMahasiswaApiService(retrofit: Retrofit): MonitoringMahasiswaApiService {
        return retrofit.create(MonitoringMahasiswaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMonitoringMahasiswaRepository(
        apiService: MonitoringMahasiswaApiService
    ): MonitoringMahasiswaRepository {
        return MonitoringMahasiswaRepositoryImpl(apiService)
    }
}
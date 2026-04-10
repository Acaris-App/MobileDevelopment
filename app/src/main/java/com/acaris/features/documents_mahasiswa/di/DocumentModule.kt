package com.acaris.features.documents_mahasiswa.di

import com.acaris.features.documents_mahasiswa.data.remote.datasource.DocumentApiService
import com.acaris.features.documents_mahasiswa.data.repository.DocumentRepositoryImpl
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocumentModule {

    @Provides
    @Singleton
    fun provideDocumentApiService(retrofit: Retrofit): DocumentApiService {
        return retrofit.create(DocumentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDocumentRepository(
        apiService: DocumentApiService
    ): DocumentRepository {
        return DocumentRepositoryImpl(apiService)
    }
}
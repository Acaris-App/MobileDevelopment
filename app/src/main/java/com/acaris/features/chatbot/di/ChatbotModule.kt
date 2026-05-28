package com.acaris.features.chatbot.di

import com.acaris.features.chatbot.data.remote.datasource.ChatbotApiService
import com.acaris.features.chatbot.data.repository.ChatbotRepositoryImpl
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatbotModule {

    @Provides
    @Singleton
    fun provideChatbotApiService(retrofit: Retrofit): ChatbotApiService {
        return retrofit.create(ChatbotApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatbotRepository(apiService: ChatbotApiService): ChatbotRepository {
        return ChatbotRepositoryImpl(apiService)
    }
}
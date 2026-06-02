package com.acaris.features.monitoring_mahasiswa.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.chatbot.data.remote.model.ChatHistoryItemResponse
import com.acaris.features.chatbot.data.remote.model.ChatSessionResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.DetailMahasiswaResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.MahasiswaBimbinganResponse
import com.acaris.features.monitoring_mahasiswa.data.remote.model.RiwayatBimbinganResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MonitoringMahasiswaApiService {

    @GET("dosen/mahasiswa")
    suspend fun getDaftarMahasiswa(): BaseResponse<List<MahasiswaBimbinganResponse>>

    @GET("dosen/mahasiswa/{mahasiswaId}/detail")
    suspend fun getDetailMahasiswa(
        @Path("mahasiswaId") mahasiswaId: String
    ): BaseResponse<DetailMahasiswaResponse>

    @GET("dosen/mahasiswa/{mahasiswaId}/history-bimbingan")
    suspend fun getRiwayatBimbingan(
        @Path("mahasiswaId") mahasiswaId: String
    ): BaseResponse<List<RiwayatBimbinganResponse>>

    @GET("dosen/mahasiswa/{mahasiswaId}/chatbot")
    suspend fun getMahasiswaChatbotHistory(
        @Path("mahasiswaId") mahasiswaId: String
    ): BaseResponse<List<ChatHistoryItemResponse>>

    @GET("dosen/mahasiswa/{mahasiswaId}/chatbot/{sessionId}")
    suspend fun getMahasiswaChatbotDetail(
        @Path("mahasiswaId") mahasiswaId: String,
        @Path("sessionId") sessionId: String
    ): BaseResponse<ChatSessionResponse>
}
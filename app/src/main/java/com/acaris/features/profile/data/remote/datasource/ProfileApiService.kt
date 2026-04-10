package com.acaris.features.profile.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.profile.data.remote.model.PhotoUpdateResponse
import com.acaris.features.profile.data.remote.model.ProfileResponse
import com.acaris.features.profile.data.remote.model.UpdateProfileRequest
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApiService {

    @GET("user/profile")
    suspend fun getProfile(): BaseResponse<ProfileResponse>

    @PUT("user/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): BaseResponse<ProfileResponse>

    @Multipart
    @POST("user/profile/photo") // Bisa diganti PUT tergantung standar Backend-mu
    suspend fun updateProfilePhoto(
        @Part photo: MultipartBody.Part
    ): BaseResponse<PhotoUpdateResponse>
}
package com.acaris.features.user_management.data.remote.datasource

import com.acaris.features.user_management.data.remote.model.UserResponse // 🌟 Update import
import com.acaris.core.network.model.BaseResponse
import retrofit2.http.*

interface UserManagementApiService {

    @GET("admin/users")
    suspend fun getUsers(
        @Query("role") role: String,
        @Query("search") search: String?,
        @Query("sort_by") sortBy: String?
    ): BaseResponse<List<UserResponse>>

    @FormUrlEncoded
    @POST("admin/users/admin")
    suspend fun addAdmin(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): BaseResponse<UserResponse>

    @FormUrlEncoded
    @PUT("admin/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("identifier") identifier: String?
    ): BaseResponse<UserResponse>

    @FormUrlEncoded
    @PATCH("admin/users/{id}/status")
    suspend fun changeUserStatus(
        @Path("id") id: String,
        @Field("is_active") isActive: Boolean
    ): BaseResponse<Any>

    @DELETE("admin/users/{id}")
    suspend fun deleteUserPermanently(
        @Path("id") id: String
    ): BaseResponse<Any>
}
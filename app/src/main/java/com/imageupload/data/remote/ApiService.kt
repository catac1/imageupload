package com.imageupload.data.remote

import com.imageupload.data.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/api/upload/save")
    suspend fun uploadImage(
        @Part("code") code: RequestBody,
        @Part image: MultipartBody.Part,
    ): UploadResponse
}
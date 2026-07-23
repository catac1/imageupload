package com.imageupload.data.remote

import com.imageupload.data.model.remote.ApiService
import retrofit2.Retrofit

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://your-server.com/")
            .build()
            .create(ApiService::class.java)
    }
}

package com.example.networkcalls.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface UploadPhotoApi {

    // Doesn't work! Replace @Multipart with @FormUrlEncoded
    @Multipart
    @PATCH("photos")
    fun uploadImage(
        @Part photo: MultipartBody.Part
    ): Call<UploadResponse>

    companion object {

        private val client = OkHttpClient.Builder().apply {
            addInterceptor(FormDataInterceptor())
        }.build()

        operator fun invoke(): UploadPhotoApi {
            return Retrofit.Builder()
                .baseUrl("https://cardiary.herokuapp.com/api/v1/cars/1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadPhotoApi::class.java)
        }
    }
}
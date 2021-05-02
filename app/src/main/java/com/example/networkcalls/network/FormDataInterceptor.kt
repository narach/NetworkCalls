package com.example.networkcalls.network

import okhttp3.Interceptor
import okhttp3.Response

class FormDataInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "multipart/form-data;boundary=imageRequest${System.currentTimeMillis()}")
            .build()
        return chain.proceed(request)
    }
}
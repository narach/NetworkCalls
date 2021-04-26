package com.example.networkcalls.network

import com.example.networkcalls.utils.Constants
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Объявляем как lazy, чтобы объект инициализировался при первом обращении
    val api: TodoApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.sampleApiUrl) // Указываем базовую ссылку на API
            .addConverterFactory(GsonConverterFactory.create()) // Для преобразования JSON в объект
            .build()
            .create(TodoApi::class.java) // Предоставляем API класс
    }

    val postsApi: PostsApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.sampleApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PostsApi::class.java)
    }

    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.openWeatherBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
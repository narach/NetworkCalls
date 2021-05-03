package com.example.networkcalls.network

import com.example.networkcalls.network.api.CarsApi
import com.example.networkcalls.network.api.PostsApi
import com.example.networkcalls.network.api.TodoApi
import com.example.networkcalls.network.api.WeatherApi
import com.example.networkcalls.network.interceptors.MyInterceptor
import com.example.networkcalls.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(MyInterceptor())
    }.build()

    // Объявляем как lazy, чтобы объект инициализировался при первом обращении
    val api: TodoApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.sampleApiUrl) // Указываем базовую ссылку на API
            .addConverterFactory(GsonConverterFactory.create()) // Для преобразования JSON в объект
            .build()
            .create(TodoApi::class.java) // Предоставляем API класс
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.sampleApiUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val postsApi: PostsApi by lazy {
        retrofit.create(PostsApi::class.java)
    }

    val carsApi: CarsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://cardiary.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarsApi::class.java)
    }

    val postsApi2: PostsApi by lazy {
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
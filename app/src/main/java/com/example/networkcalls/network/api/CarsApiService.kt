package com.example.networkcalls.network.api

import com.example.networkcalls.entities.cardiary.Car
import retrofit2.Response
import retrofit2.http.GET

interface CarsApiService {

    @GET("cars")
    suspend fun getCars(): Response<List<Car>>
}
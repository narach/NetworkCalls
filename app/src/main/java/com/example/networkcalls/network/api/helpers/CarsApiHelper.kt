package com.example.networkcalls.network.api.helpers

import com.example.networkcalls.entities.cardiary.Car
import retrofit2.Response

interface CarsApiHelper {
    suspend fun getCars(): Response<List<Car>>
}
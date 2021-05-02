package com.example.networkcalls.repositories

import com.example.networkcalls.entities.cardiary.Car
import com.example.networkcalls.network.RetrofitInstance
import retrofit2.Response

class CarsRepository {
    suspend fun getCars(): Response<List<Car>> {
        return RetrofitInstance.carsApi.getCars()
    }
}
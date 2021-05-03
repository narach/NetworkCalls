package com.example.networkcalls.network.api.helpers

import com.example.networkcalls.entities.cardiary.Car
import com.example.networkcalls.network.api.CarsApiService
import retrofit2.Response
import javax.inject.Inject

class CarsApiHelperImpl @Inject constructor(
    private val carsApiService: CarsApiService
) : CarsApiHelper {
    override suspend fun getCars(): Response<List<Car>> {
        return carsApiService.getCars()
    }
}
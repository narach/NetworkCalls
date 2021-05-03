package com.example.networkcalls.network.repositories

import com.example.networkcalls.network.api.helpers.CarsApiHelper
import javax.inject.Inject

class CarsRepository @Inject constructor(
    private val carsApiHelper: CarsApiHelper
) {
    suspend fun getCars() = carsApiHelper.getCars()
}
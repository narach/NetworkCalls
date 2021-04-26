package com.example.networkcalls.network

import com.example.networkcalls.entities.Weather
import com.example.networkcalls.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherInGomel(
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
        @Query("q") city: String = "gomel",
        @Query("appid") apiKey: String = Constants.openWeatherApiKey
    ) : Response<Weather>
}
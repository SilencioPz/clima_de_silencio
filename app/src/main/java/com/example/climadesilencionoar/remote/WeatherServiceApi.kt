package com.example.climadesilencionoar.remote

import com.example.climadesilencionoar.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceApi {
    @GET("2.5/weather")
    suspend fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") metric: String
    ): WeatherResponse

    @GET("2.5/weather")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("appid") appId: String,
        @Query("units") metric: String
    ): WeatherResponse
}
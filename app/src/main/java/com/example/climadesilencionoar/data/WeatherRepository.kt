package com.example.climadesilencionoar.data

import com.example.climadesilencionoar.models.WeatherResponse
import com.example.climadesilencionoar.utils.Constants
import com.example.climadesilencionoar.remote.WeatherServiceApi
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WeatherRepository @Inject constructor() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherServiceApi::class.java)

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return suspendCancellableCoroutine { continuation ->
            service.getWeatherDetails(lat, lon, Constants.APP_ID, Constants.UNIDADE_METRICA)
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body()!!)
                        } else {
                            continuation.resumeWithException(Exception("Failed to get weather data"))
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }
}

package com.example.climadesilencionoar.data

import android.util.Log
import android.util.LruCache
import com.example.climadesilencionoar.BuildConfig
import com.example.climadesilencionoar.models.WeatherResponse
import com.example.climadesilencionoar.remote.WeatherServiceApi
import com.example.climadesilencionoar.utils.Constants
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherServiceApi
) {
    private val cache = LruCache<Pair<Double, Double>, CachedWeatherData>(10)

    data class CachedWeatherData(
        val data: WeatherResponse,
        val timestamp: Long
    )

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val cacheKey = Pair(lat, lon)

            cache[cacheKey]?.let { cached ->
                if (System.currentTimeMillis() - cached.timestamp < 5 * 60 * 1000) {
                    Log.d("CACHE", "Retornando dados do cache")
                    return Result.success(cached.data)
                } else {
                    Log.d("CACHE", "Cache expirado, removendo")
                    cache.remove(cacheKey)
                }
            }

            val response = withTimeout(5_000) {
                api.getWeatherDetails(
                    latitude = lat,
                    longitude = lon,
                    appId = Constants.APP_ID,
                    metric = Constants.UNIDADE_METRICA
                )
            }

            cache.put(cacheKey, CachedWeatherData(response, System.currentTimeMillis()))
            Log.d("API", "Dados atualizados do servidor")
            Result.success(response)

        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e("API_TIMEOUT", "Timeout na requisição após 5s", e)
            Result.failure(Exception("Timeout: Verifique sua conexão"))
        } catch (e: java.net.UnknownHostException) {
            Log.e("API_NETWORK", "Sem conexão com a internet", e)
            Result.failure(Exception("Sem conexão com a internet"))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("API_SOCKET", "Timeout de conexão", e)
            Result.failure(Exception("Conexão muito lenta"))
        } catch (e: Exception) {
            Log.e("API_ERROR", "Falha na requisição: ${e.message}", e)
            Result.failure(Exception("Erro ao buscar dados: ${e.message}"))
        }
    }

    suspend fun getWeatherByCityName(cityName: String): Result<WeatherResponse> {
        return try {
            val response = api.getWeatherByCityName(
                cityName = cityName,
                appId = BuildConfig.WEATHER_API_KEY,
                metric = "metric"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.example.climadesilencionoar.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.example.climadesilencionoar.BuildConfig

object Constants {
    const val APP_ID: String = BuildConfig.WEATHER_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/data/"
    const val UNIDADE_METRICA = "metric"
    const val LATITUDE_PADRAO = -16.4706  // Latitude de Rondonópolis
    const val LONGITUDE_PADRAO = -54.6356

    init {
        Log.d("API_KEY_DEBUG", "Chave carregada: ${APP_ID.take(3)}...${APP_ID.takeLast(3)}")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun verificarConexaoInternet(contexto: Context): Boolean {

        // Adicione este log temporário para debug
        Log.d("API_DEBUG", "API Key: ${Constants.APP_ID}")
        Log.d("API_DEBUG", "API Key length: ${Constants.APP_ID.length}")
        Log.d("API_DEBUG", "API Key is empty: ${Constants.APP_ID.isEmpty()}")

        val gerenciadorConexao = contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val rede = gerenciadorConexao.activeNetwork ?: return false
        val redeAtiva = gerenciadorConexao.getNetworkCapabilities(rede) ?: return false

        return when {
            redeAtiva.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            redeAtiva.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            redeAtiva.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}

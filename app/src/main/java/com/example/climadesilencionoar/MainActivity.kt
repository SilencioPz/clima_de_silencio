package com.example.climadesilencionoar

//IMPORTANTE: colocar local.properties no GitIgnore!!!

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.climadesilencionoar.ui.theme.ClimaDeSilencioAppTheme
import com.example.climadesilencionoar.ui.theme.MainScreen
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.climadesilencionoar.models.WeatherResponse
import com.example.climadesilencionoar.remote.WeatherServiceApi
import com.example.climadesilencionoar.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            ClimaDeSilencioAppTheme {
                var weatherState by remember {
                    mutableStateOf(WeatherState())
                }
                var showPermissionDialog by remember { mutableStateOf(false) }
                MainScreen(
                    weatherData = weatherState.data,
                    isLoading = weatherState.isLoading,
                    errorMessage = weatherState.errorMessage,
                    onRequestLocation = {
                        if (checkLocationPermission()) {
                            weatherState = weatherState.copy(isLoading = true)
                            buscarClimaAtual { response, error ->
                                weatherState = if (error != null) {
                                    WeatherState(errorMessage = error)
                                } else {
                                    WeatherState(data = response)
                                }
                            }
                        } else {
                            showPermissionDialog = true
                        }
                    }
                )

                if (showPermissionDialog) {
                    AlertDialog(
                        onDismissRequest = { showPermissionDialog = false },
                        title = { Text("Permissão de Localização") },
                        text = { Text("O app precisa da sua localização para mostrar o clima atual.") },
                        confirmButton = {
                            Button(onClick = {
                                requestLocationPermission()
                                showPermissionDialog = false
                            }) {
                                Text("Permitir")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                buscarClimaAtual { dados, erro -> /* ... */ }
                                showPermissionDialog = false
                            }) {
                                Text("Usar local padrão")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
         return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun buscarLocalizacaoAtual(callback: (Double, Double) -> Unit) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        callback(it.latitude, it.longitude)
                    } ?: run {
                        Log.w("LOCATION", "Usando localização padrão")
                        callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
                    }
                }
        } else {
            callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
        }
    }

    private fun buscarClimaAtual(callback: (WeatherResponse?, String?) -> Unit) {
        buscarLocalizacaoAtual { lat, lon ->
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(WeatherServiceApi::class.java)
            val call = service.getWeatherDetails(lat, lon, Constants.APP_ID, Constants.UNIDADE_METRICA)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>,
                                        response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Erro: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    callback(null, "Falha: ${t.message}")
                }
            })
        }
    }
}

data class WeatherState(
    val data: WeatherResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

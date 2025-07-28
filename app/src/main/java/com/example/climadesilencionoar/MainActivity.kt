package com.example.climadesilencionoar

import android.os.Handler
import androidx.lifecycle.lifecycleScope
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.climadesilencionoar.ui.theme.ClimaDeSilencioAppTheme
import com.example.climadesilencionoar.ui.theme.MainScreen
import com.example.climadesilencionoar.ui.theme.WeatherViewModel
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.climadesilencionoar.utils.Constants
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.climadesilencionoar.ui.theme.WeatherState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            ClimaDeSilencioAppTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                var showPermissionDialog by remember { mutableStateOf(false) }

                val weatherData = (state as? WeatherState.Success)?.data
                val isLoading = state is WeatherState.Loading
                val errorMessage = (state as? WeatherState.Error)?.message

                MainScreen(
                    viewModel = viewModel,
                    weatherData = weatherData,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onRequestLocation = {
                        if (checkLocationPermission()) {
                            scope.launch {
                                buscarLocalizacaoAtual { lat, lon ->
                                    viewModel.fetchWeather(lat, lon)
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
                                scope.launch {
                                    viewModel.fetchWeather()
                                }
                                showPermissionDialog = false
                            }) {
                                Text("Usar local padrão")
                            }
                        }
                    )
                }

                LaunchedEffect(Unit) {
                    if (checkLocationPermission()) {
                        buscarLocalizacaoAtual { lat, lon ->
                            viewModel.fetchWeather(lat, lon)
                        }
                    } else {
                        viewModel.fetchWeather()
                    }
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
        if (!checkLocationPermission()) {
            callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
            lastLocation?.let {
                callback(it.latitude, it.longitude)
                return@addOnSuccessListener
            }

            buscarNovaLocalizacao(callback)
        }.addOnFailureListener {
            Log.w("LOCATION", "Erro ao buscar última localização", it)
            callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
        }
    }

    @SuppressLint("MissingPermission")
    private fun buscarNovaLocalizacao(callback: (Double, Double) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            500
        ).apply {
            setDurationMillis(2000)
            setMaxUpdates(1)
        }.build()

        var callbackExecuted = false

        val timeoutRunnable = Runnable {
            if (!callbackExecuted) {
                callbackExecuted = true
                Log.w("LOCATION", "Timeout - usando localização padrão")
                callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
                callbackExecuted = true
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeoutRunnable, 3000)

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    handler.removeCallbacks(timeoutRunnable)
                    if (!callbackExecuted) {
                        callbackExecuted = true
                        locationResult.lastLocation?.let {
                            callback(it.latitude, it.longitude)
                        } ?: run {
                            Log.w("LOCATION", "Localização nula - usando padrão")
                            callback(Constants.LATITUDE_PADRAO, Constants.LONGITUDE_PADRAO)
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Permissão concedida - buscando localização")
                lifecycleScope.launch {
                    buscarLocalizacaoAtual { lat, lon ->
                        viewModel.fetchWeather(lat, lon)
                    }
                }
            } else {
                Log.d("PERMISSION", "Permissão negada - usando localização padrão")
                lifecycleScope.launch {
                    viewModel.fetchWeather()
                }
            }
        }
    }
}
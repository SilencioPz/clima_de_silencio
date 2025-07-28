package com.example.climadesilencionoar.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climadesilencionoar.data.WeatherRepository
import com.example.climadesilencionoar.models.WeatherResponse
import com.example.climadesilencionoar.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _state = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _capitaisData = MutableStateFlow<Map<String, WeatherResponse>>(emptyMap())
    val capitaisData = _capitaisData.asStateFlow()

    private var lastValidData: WeatherResponse? = null

    fun fetchWeather(lat: Double? = null, lon: Double? = null) {
        viewModelScope.launch {
            lastValidData?.let {
                _state.value = WeatherState.Success(it)
            } ?: run {
                _state.value = WeatherState.Loading
            }

            try {
                val currentLat = lat ?: Constants.LATITUDE_PADRAO
                val currentLon = lon ?: Constants.LONGITUDE_PADRAO

                repository.getWeather(currentLat, currentLon)
                    .onSuccess { response ->
                        lastValidData = response
                        _state.value = WeatherState.Success(response)
                    }
                    .onFailure { e ->
                        _state.value = WeatherState.Error(e.message)
                    }
            } catch (e: Exception) {
                _state.value = WeatherState.Error(e.message)
            }
        }
    }

    fun fetchCapitalWeather(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                repository.getWeather(lat, lon).onSuccess { response ->
                    _capitaisData.update { currentMap ->
                        currentMap + (cityName to response)
                    }
                }
            } catch (e: Exception) {
                // Log do erro se necessário
            }
        }
    }

    fun fetchWeatherByCityName(cityName: String) {
        viewModelScope.launch {
            _state.value = WeatherState.Loading
            try {
                repository.getWeatherByCityName(cityName)
                    .onSuccess { response ->
                        lastValidData = response
                        _state.value = WeatherState.Success(response)
                    }
                    .onFailure { e ->
                        _state.value = WeatherState.Error(e.message ?: "Cidade não encontrada")
                    }
            } catch (e: Exception) {
                _state.value = WeatherState.Error("Erro na conexão")
            }
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String?) : WeatherState()
}
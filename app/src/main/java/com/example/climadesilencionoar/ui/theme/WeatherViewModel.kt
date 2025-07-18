//package com.example.climadesilencionoar.ui.theme
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.climadesilencionoar.data.WeatherRepository
//import com.example.climadesilencionoar.models.WeatherResponse
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class WeatherViewModel @Inject constructor(
//    private val repository: WeatherRepository
//) : ViewModel() {
//    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
//    val weatherState: StateFlow<WeatherResponse?> = _weatherState
//
//    private val _capitaisState = MutableStateFlow<Map<String, WeatherResponse?>?>(null)
//    val capitaisState: StateFlow<Map<String, WeatherResponse?>?> = _capitaisState
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    fun fetchWeather(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val weather = repository.getWeather(lat, lon)
//                _weatherState.value = weather
//            } catch (e: Exception) {
//                _weatherState.value = null
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun fetchCapitais() {
//        viewModelScope.launch {
//            val capitais = listOf(
//                "São Paulo" to "-23.5505,-46.6333",
//                "Rio de Janeiro" to "-22.9068,-43.1729",
//                "Belo Horizonte" to "-19.9191,-43.9387",
//                "Curitiba" to "-25.4296,-49.2719",
//                "Brasília" to "-15.7801,-47.9292"
//            )
//            val results = mutableMapOf<String, WeatherResponse?>()
//
//            capitais.forEach { (nome, coord) ->
//                val (lat, lon) = coord.split(",")
//                try {
//                    val weather = repository.getWeather(lat.toDouble(), lon.toDouble())
//                    results[nome] = weather
//                } catch (e: Exception) {
//                    results[nome] = null
//                }
//            }
//            _capitaisState.value = results
//        }
//    }
//}

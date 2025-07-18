package com.example.climadesilencionoar.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.climadesilencionoar.R
import com.example.climadesilencionoar.models.WeatherResponse
import com.example.climadesilencionoar.remote.WeatherServiceApi
import com.example.climadesilencionoar.utils.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun MainScreen(
    weatherData: WeatherResponse? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRequestLocation: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.silenciopz_logo),
            contentDescription = "Logo do App",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CapitaisTemperaturaList()

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
                Text("Carregando clima...", style = MaterialTheme.typography.bodyMedium)
            }
            errorMessage != null -> {
                Text("Erro: $errorMessage", color = MaterialTheme.colorScheme.error)
                Button(onClick = onRequestLocation) {
                    Text("Tentar novamente")
                }
            }
            weatherData != null -> {
                WeatherInfo(weatherData)
            }
            else -> {
                Button(onClick = onRequestLocation) {
                    Text("Obter clima atual")
                }
            }
        }
    }
}

@Composable
fun CapitaisTemperaturaList() {
    val capitais = listOf(
        "São Paulo" to "-23.5505,-46.6333",
        "Rio de Janeiro" to "-22.9068,-43.1729",
        "Belo Horizonte" to "-19.9191,-43.9387",
        "Curitiba" to "-25.4296,-49.2719",
        "Brasília" to "-15.7801,-47.9292"
    )

    var capitaisClima by remember { mutableStateOf<Map<String,
            WeatherResponse?>>(emptyMap()) }

    LaunchedEffect(Unit) {
        capitais.forEach { (nome, coordenadas) ->
            val (lat, lon) = coordenadas.split(",")
            fetchClimaPorCoordenadas(lat.toDouble(), lon.toDouble()) { resposta ->
                capitaisClima = capitaisClima + (nome to resposta)
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(capitais) { (cidade, _) ->
            Card(
                modifier = Modifier.width(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(cidade, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        capitaisClima[cidade]?.main?.temp?.toInt()?.let { "$it°C" } ?: "--",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherInfo(weather: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = "Localização",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${weather.name}, ${weather.sys.country}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${weather.main.temp.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = traduzirDescricao(weather.weather[0].description),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GridLayout(weather)
        }
    }
}

fun formatTimeWithEmoji(time: Long, isSunrise: Boolean, timezoneOffset: Int = 0): String {
    val emoji = if (isSunrise) "☀️" else "🌙"
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    val adjustedTime = time * 1000L
    sdf.timeZone = TimeZone.getTimeZone("GMT${if (timezoneOffset >= 0) "+" else ""}${timezoneOffset / 3600}")
    return "$emoji ${sdf.format(Date(adjustedTime))}"
}

@Composable
private fun GridLayout(weather: WeatherResponse) {
    val items = listOf(
        WeatherInfoItem("Sensação", "${weather.main.feels_like.toInt()}°C", R.drawable.ic_thermo),
        WeatherInfoItem("Umidade", "${weather.main.humidity}%", R.drawable.ic_humidity),
        WeatherInfoItem("Pressão", "${weather.main.pressure}hPa", R.drawable.ic_pressure),
        WeatherInfoItem("Vento", "${weather.wind.speed} km/h", R.drawable.ic_wind),
        WeatherInfoItem(
            "Nascer do sol",
            formatTimeWithEmoji(weather.sys.sunrise.toLong(), true, weather.timezone),
            R.drawable.ic_sunrise
        ),
        WeatherInfoItem(
            "Pôr do sol",
            formatTimeWithEmoji(weather.sys.sunset.toLong(), false, weather.timezone),
            R.drawable.ic_sunset
        )
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            WeatherCard(item)
        }
    }
}

@Composable
fun WeatherCard(item: WeatherInfoItem) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.label,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

fun traduzirDescricao(descricao: String): String {
    return when(descricao.lowercase()) {
        "broken clouds" -> "Nuvens dispersas"
        "clear sky" -> "Céu limpo"
        "few clouds" -> "Poucas nuvens"
        "scattered clouds" -> "Nuvens dispersas"
        "overcast clouds" -> "Nublado"
        "light rain" -> "Chuva leve"
        "moderate rain" -> "Chuva moderada"
        "heavy intensity rain" -> "Chuva intensa"
        else -> descricao.replaceFirstChar { it.uppercase() }
    }
}

private fun fetchClimaPorCoordenadas(
    lat: Double,
    lon: Double,
    callback: (WeatherResponse?) -> Unit
) {
    val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val service = retrofit.create(WeatherServiceApi::class.java)
    service.getWeatherDetails(lat, lon, Constants.APP_ID, Constants.UNIDADE_METRICA)
        .enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(call: retrofit2.Call<WeatherResponse>, response: retrofit2.Response<WeatherResponse>) {
                callback(response.body())
            }
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                callback(null)
            }
        })
}

data class WeatherInfoItem(val label: String, val value: String, val icon: Int)

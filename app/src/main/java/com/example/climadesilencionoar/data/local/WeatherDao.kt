package com.example.climadesilencionoar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun saveWeather(weather: WeatherEntity)

    @Query("SELECT * FROM WeatherEntity WHERE cityId = :id")
    suspend fun getWeather(id: Int): WeatherEntity?
}
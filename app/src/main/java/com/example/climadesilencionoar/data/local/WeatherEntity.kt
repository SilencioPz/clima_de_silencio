package com.example.climadesilencionoar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity(
    @PrimaryKey val cityId: Int,
    val data: String
)
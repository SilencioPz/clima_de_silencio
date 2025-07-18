package com.example.climadesilencionoar.models

data class Sys(
    val type: Int?,
    val id: Int?,
    val country: String,
    val sunrise: Int,
    val sunset: Int,
    val message: String? = null
)

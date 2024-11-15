// app/src/main/java/com/example/api_meteo/repository/WeatherRepository.kt
package com.example.api_meteo.repository

import com.example.api_meteo.model.WeatherResponse
import com.example.api_meteo.network.RetrofitInstance

class WeatherRepository {
    suspend fun getWeather(deptNumber: String): WeatherResponse {
        return RetrofitInstance.api.getWeather(deptNumber)
    }

    suspend fun getWeatherByLocation(latitude: Double, longitude: Double): WeatherResponse {
        return RetrofitInstance.api.getWeatherByLocation(latitude, longitude)
    }
}

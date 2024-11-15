// app/src/main/java/com/example/api_meteo/model/WeatherResponse.kt
package com.example.api_meteo.model

data class WeatherResponse(
    val status: String,
    val weather: WeatherData? = null,
    val message: String? = null
)

data class WeatherData(
    val department: String? = null,     // Optionnel pour les requêtes par département
    val latitude: Double? = null,       // Optionnel pour les requêtes GPS
    val longitude: Double? = null,      // Optionnel pour les requêtes GPS
    val temperature: Double?,
    val windspeed: Double?,
    val winddirection: Int?,
    val weathercode: String?,
    val time: String?
)

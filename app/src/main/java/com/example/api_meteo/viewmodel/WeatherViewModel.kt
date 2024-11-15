// app/src/main/java/com/example/api_meteo/viewmodel/WeatherViewModel.kt
package com.example.api_meteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_meteo.model.WeatherData
import com.example.api_meteo.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherState {
    object Empty : WeatherState()
    object Loading : WeatherState()
    data class Success(val weather: WeatherData) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val weatherState: StateFlow<WeatherState> = _weatherState

    fun fetchWeather(deptNumber: String) {
        _weatherState.value = WeatherState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getWeather(deptNumber)
                if (response.status == "success" && response.weather != null) {
                    _weatherState.value = WeatherState.Success(response.weather)
                } else {
                    _weatherState.value = WeatherState.Error(response.message ?: "Erreur inconnue")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun fetchWeatherWithLocation(latitude: Double, longitude: Double) {
        _weatherState.value = WeatherState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getWeatherByLocation(latitude, longitude)
                if (response.status == "success" && response.weather != null) {
                    _weatherState.value = WeatherState.Success(response.weather)
                } else {
                    _weatherState.value = WeatherState.Error(response.message ?: "Erreur inconnue")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun resetState() {
        _weatherState.value = WeatherState.Empty
    }
}

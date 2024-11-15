// app/src/main/java/com/example/api_meteo/ui/WeatherScreen.kt
package com.example.api_meteo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.api_meteo.model.WeatherData
import com.example.api_meteo.viewmodel.WeatherState
import com.example.api_meteo.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    onFetchWeatherWithLocation: () -> Unit
) {
    var deptNumber by remember { mutableStateOf("") }

    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Récupérer la Météo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Champ de saisie pour le numéro de département
        OutlinedTextField(
            value = deptNumber,
            onValueChange = {
                deptNumber = it
                if (it.isEmpty()) {
                    viewModel.resetState()
                }
            },
            label = { Text("Numéro du Département") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Bouton pour obtenir la météo par département
        Button(
            onClick = { viewModel.fetchWeather(deptNumber) },
            enabled = deptNumber.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Obtenir la météo par département")
        }

        // Bouton pour obtenir la météo par localisation GPS
        Button(
            onClick = { onFetchWeatherWithLocation() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Obtenir la météo par localisation GPS")
        }

        when (weatherState) {
            is WeatherState.Empty -> {
                // Rien à afficher
            }
            is WeatherState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is WeatherState.Success -> {
                val weather = (weatherState as WeatherState.Success).weather
                WeatherCard(weather)
            }
            is WeatherState.Error -> {
                val message = (weatherState as WeatherState.Error).message
                Text(
                    text = "Erreur: $message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Affichage des informations météorologiques

            // Affichage du département si disponible
            weather.department?.let {
                Text(
                    text = "Département: $it",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Affichage des coordonnées GPS si disponibles
            if (weather.latitude != null && weather.longitude != null) {
                Text(
                    text = "Coordonnées: ${weather.latitude}, ${weather.longitude}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Affichage des autres informations
            weather.temperature?.let { temp ->
                Text(text = "Température: $temp°C", style = MaterialTheme.typography.bodyLarge)
            }
            weather.windspeed?.let { windspeed ->
                Text(text = "Vitesse du vent: $windspeed km/h", style = MaterialTheme.typography.bodyLarge)
            }
            weather.winddirection?.let { winddir ->
                Text(text = "Direction du vent: $winddir°", style = MaterialTheme.typography.bodyLarge)
            }
            weather.weathercode?.let { code ->
                Text(text = "Code météo: $code", style = MaterialTheme.typography.bodyLarge)
            }
            weather.time?.let { time ->
                Text(text = "Heure: $time", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

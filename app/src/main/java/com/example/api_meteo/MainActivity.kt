// app/src/main/java/com/example/api_meteo/ui/MainActivity.kt
package com.example.api_meteo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.example.api_meteo.ui.theme.ApiMeteoTheme
import com.example.api_meteo.ItemScreen
import com.example.api_meteo.viewmodel.ItemViewModel
import com.example.api_meteo.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onFetchWeatherWithLocation: () -> Unit,
    weatherViewModel: WeatherViewModel,
    itemViewModel: ItemViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Météo & Items") }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Météo") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Items") }
                    )
                }
                when (selectedTab) {
                    0 -> WeatherScreen(weatherViewModel, onFetchWeatherWithLocation)
                    1 -> ItemScreen(itemViewModel)
                }
            }
        }
    )
}

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val itemViewModel: ItemViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            getLastLocationAndFetchWeather()
        } else {
            showToast("Permission de localisation refusée.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            ApiMeteoTheme {
                MainScreen(
                    onFetchWeatherWithLocation = { checkLocationPermissionAndFetchWeather() },
                    weatherViewModel = weatherViewModel,
                    itemViewModel = itemViewModel
                )
            }
        }
    }

    private fun checkLocationPermissionAndFetchWeather() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocationAndFetchWeather()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLastLocationAndFetchWeather() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    weatherViewModel.fetchWeatherWithLocation(latitude, longitude)
                } else {
                    showToast("Localisation non disponible.")
                }
            }
            .addOnFailureListener { exception ->
                showToast("Erreur de localisation: ${exception.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

// app/src/main/java/com/example/api_meteo/network/ApiService.kt
package com.example.api_meteo.network

import com.example.api_meteo.model.Item
import com.example.api_meteo.model.ItemResponse
import com.example.api_meteo.model.WeatherResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/get-weather")
    suspend fun getWeather(
        @Query("dept_number") deptNumber: String
    ): WeatherResponse

    @GET("/api/get-weather-gps")
    suspend fun getWeatherByLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): WeatherResponse


    // Récupérer la liste des Items
    @GET("/api/items")
    suspend fun getItems(): ItemResponse

    // Ajouter un nouvel Item
    @POST("/api/item")
    suspend fun addItem(@Body item: Item): ItemResponse

    // Modifier un Item existant
    @PUT("/api/item/{id}")
    suspend fun updateItem(@Path("id") id: Int, @Body item: Map<String, String>): ItemResponse

    // Supprimer un Item
    @DELETE("/api/item/{id}")
    suspend fun deleteItem(@Path("id") id: Int): ItemResponse
}

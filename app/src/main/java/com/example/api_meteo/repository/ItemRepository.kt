// app/src/main/java/com/example/api_meteo/repository/ItemRepository.kt
package com.example.api_meteo.repository

import com.example.api_meteo.model.Item
import com.example.api_meteo.model.ItemResponse
import com.example.api_meteo.network.RetrofitInstance

class ItemRepository {
    suspend fun getItems(): List<Item> {
        val response = RetrofitInstance.api.getItems()
        return response.items.data // Accéder à la liste "data"
    }

    suspend fun addItem(item: Item): Item {
        val response = RetrofitInstance.api.addItem(item)
        return response.items.data.first() // Retourne l'item ajouté
    }

    suspend fun updateItem(id: Int, nom: String): ItemResponse {
        return RetrofitInstance.api.updateItem(id, mapOf("nom" to nom))
    }

    suspend fun deleteItem(id: Int): ItemResponse {
        return RetrofitInstance.api.deleteItem(id)
    }
}

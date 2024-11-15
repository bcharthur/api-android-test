// app/src/main/java/com/example/api_meteo/model/ItemResponse.kt
package com.example.api_meteo.model

data class ItemResponse(
    val status: String,
    val items: ItemDataResponse
)

data class ItemDataResponse(
    val data: List<Item>
)

data class Item(
    val id: Int? = null, // ID généré par le serveur, optionnel lors de la création
    val nom: String
)

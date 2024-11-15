// app/src/main/java/com/example/api_meteo/viewmodel/ItemViewModel.kt
package com.example.api_meteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_meteo.model.Item
import com.example.api_meteo.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ItemState {
    object Empty : ItemState()
    object Loading : ItemState()
    data class Success(val items: List<Item>) : ItemState()
    data class Error(val message: String) : ItemState()
}

class ItemViewModel : ViewModel() {
    private val repository = ItemRepository()

    private val _itemState = MutableStateFlow<ItemState>(ItemState.Empty)
    val itemState: StateFlow<ItemState> = _itemState

    fun fetchItems() {
        _itemState.value = ItemState.Loading
        viewModelScope.launch {
            try {
                val items = repository.getItems()
                if (items.isNotEmpty()) {
                    _itemState.value = ItemState.Success(items)
                } else {
                    _itemState.value = ItemState.Empty
                }
            } catch (e: Exception) {
                _itemState.value = ItemState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun addItem(itemName: String) {
        _itemState.value = ItemState.Loading
        viewModelScope.launch {
            try {
                repository.addItem(Item(nom = itemName))
                fetchItems() // Rafraîchir la liste après ajout
            } catch (e: Exception) {
                _itemState.value = ItemState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun updateItem(id: Int, nom: String) {
        _itemState.value = ItemState.Loading
        viewModelScope.launch {
            try {
                repository.updateItem(id, nom)
                fetchItems() // Rafraîchir la liste après mise à jour
            } catch (e: Exception) {
                _itemState.value = ItemState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun deleteItem(id: Int) {
        _itemState.value = ItemState.Loading
        viewModelScope.launch {
            try {
                repository.deleteItem(id)
                fetchItems() // Rafraîchir la liste après suppression
            } catch (e: Exception) {
                _itemState.value = ItemState.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }
}

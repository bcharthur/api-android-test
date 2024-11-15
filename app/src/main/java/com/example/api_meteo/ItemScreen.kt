// app/src/main/java/com/example/api_meteo/ui/ItemScreen.kt
package com.example.api_meteo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import this
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.api_meteo.viewmodel.ItemState
import com.example.api_meteo.viewmodel.ItemViewModel

@Composable
fun ItemScreen(viewModel: ItemViewModel) {
    val itemState by viewModel.itemState.collectAsState()

    // Charger les items lorsqu'on arrive sur cet écran
    LaunchedEffect(Unit) {
        viewModel.fetchItems() // Appeler l'API pour charger la liste des items
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Gestion des Items",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Formulaire d'ajout
        var newItemName by remember { mutableStateOf("") }

        OutlinedTextField(
            value = newItemName,
            onValueChange = { newItemName = it },
            label = { Text("Nom de l'item") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (newItemName.isNotBlank()) {
                    viewModel.addItem(newItemName)
                    newItemName = "" // Réinitialiser le champ
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Ajouter")
        }

        // Liste des items
        when (itemState) {
            is ItemState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ItemState.Success -> {
                val items = (itemState as ItemState.Success).items
                LazyColumn {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.nom, style = MaterialTheme.typography.bodyLarge)
                            Row {
                                Button(
                                    onClick = { item.id?.let { viewModel.updateItem(it, "Nouveau nom") } },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Modifier")
                                }
                                Button(
                                    onClick = { item.id?.let { viewModel.deleteItem(it) } },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Supprimer")
                                }
                            }
                        }
                    }
                }
            }
            is ItemState.Error -> {
                Text(
                    text = "Erreur: ${(itemState as ItemState.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            is ItemState.Empty -> {
                Text(
                    text = "Aucun item disponible.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// app/src/main/java/com/example/api_meteo/ui/ItemScreen.kt
package com.example.api_meteo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Assurez-vous d'importer cette extension
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.api_meteo.model.Item
import com.example.api_meteo.viewmodel.ItemState
import com.example.api_meteo.viewmodel.ItemViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(viewModel: ItemViewModel = viewModel()) {
    val itemState by viewModel.itemState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Variables pour gérer les modales d'édition et de suppression
    var showEditModal by remember { mutableStateOf(false) }
    var showDeleteModal by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var newItemName by remember { mutableStateOf("") }

    // Charger les items lorsqu'on arrive sur cet écran
    LaunchedEffect(Unit) {
        viewModel.fetchItems() // Appeler l'API pour charger la liste des items
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Gestion des Items",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Formulaire d'ajout
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
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Item ajouté avec succès")
                        }
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
                            ItemRow(
                                item = item,
                                onEdit = {
                                    selectedItem = it
                                    showEditModal = true
                                },
                                onDelete = {
                                    selectedItem = it
                                    showDeleteModal = true
                                }
                            )
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

        // Modale d'édition
        if (showEditModal && selectedItem != null) {
            EditItemModal(
                item = selectedItem!!,
                onDismiss = { showEditModal = false },
                onSave = { id, newName ->
                    viewModel.updateItem(id, newName)
                    showEditModal = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Item modifié avec succès")
                    }
                }
            )
        }

        // Modale de suppression
        if (showDeleteModal && selectedItem != null) {
            DeleteItemModal(
                item = selectedItem!!,
                onDismiss = { showDeleteModal = false },
                onConfirm = { id ->
                    viewModel.deleteItem(id)
                    showDeleteModal = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Item supprimé avec succès")
                    }
                }
            )
        }
    }
}

@Composable
fun ItemRow(
    item: Item,
    onEdit: (Item) -> Unit,
    onDelete: (Item) -> Unit
) {
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
                onClick = { onEdit(item) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Modifier")
            }
            Button(
                onClick = { onDelete(item) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Supprimer")
            }
        }
    }
}

@Composable
fun EditItemModal(
    item: Item,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var editedName by remember { mutableStateOf(item.nom) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Modifier l'Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nouveau nom") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editedName.isNotBlank()) {
                        isLoading = true
                        onSave(item.id!!, editedName)
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Enregistrer")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { if (!isLoading) onDismiss() },
                enabled = !isLoading
            ) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun DeleteItemModal(
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Supprimer l'Item") },
        text = { Text("Êtes-vous sûr de vouloir supprimer cet item ?") },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    onConfirm(item.id!!)
                    isLoading = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Supprimer")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { if (!isLoading) onDismiss() },
                enabled = !isLoading
            ) {
                Text("Annuler")
            }
        }
    )
}

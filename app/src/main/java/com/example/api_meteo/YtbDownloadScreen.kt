// app/src/main/java/com/example/api_meteo/ui/YtbDownloadScreen.kt
package com.example.api_meteo.ui

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.api_meteo.viewmodel.YtbDownloadState
import com.example.api_meteo.viewmodel.YtbDownloadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YtbDownloadScreen(viewModel: YtbDownloadViewModel) {
    val downloadState by viewModel.downloadState.collectAsState()
    val context = LocalContext.current

    var ytbUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Télécharger une Vidéo YouTube",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = ytbUrl,
            onValueChange = { ytbUrl = it },
            label = { Text("URL YouTube") },
            placeholder = { Text("https://www.youtube.com/watch?v=xxxxxxx") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Définir le répertoire de téléchargement
                val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
                if (downloadDir != null) {
                    viewModel.downloadVideo(ytbUrl, downloadDir)
                } else {
                    Toast.makeText(context, "Erreur lors de l'accès au répertoire de téléchargement.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = ytbUrl.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (downloadState is YtbDownloadState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(text = "Télécharger")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (downloadState) {
            is YtbDownloadState.Success -> {
                val file = (downloadState as YtbDownloadState.Success).file
                Text(
                    text = "Téléchargement réussi !",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Fichier enregistré à : ${file.absolutePath}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.resetState() }) {
                    Text("Télécharger une autre vidéo")
                }
            }
            is YtbDownloadState.Error -> {
                val message = (downloadState as YtbDownloadState.Error).message
                Text(
                    text = "Erreur : $message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.resetState() }) {
                    Text("Réessayer")
                }
            }
            else -> {
                // Ne rien afficher dans les autres états
            }
        }
    }
}

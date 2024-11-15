// app/src/main/java/com/example/api_meteo/repository/YtbDownloadRepository.kt
package com.example.api_meteo.repository

import com.example.api_meteo.model.YtbDownloadErrorResponse
import com.example.api_meteo.network.ApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class YtbDownloadRepository(private val apiService: ApiService) {

    suspend fun downloadYouTubeVideo(ytbUrl: String, downloadDir: String): Result<File> {
        return try {
            val response: Response<ResponseBody> = apiService.downloadVideo(ytbUrl)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Extraire le nom du fichier depuis l'en-tête Content-Disposition
                    val disposition = response.headers()["Content-Disposition"]
                    val filename = disposition?.let {
                        val regex = "filename=\"?([^\"]+)\"?".toRegex()
                        regex.find(it)?.groups?.get(1)?.value ?: "video.mp4"
                    } ?: "video.mp4"

                    val file = File(downloadDir, filename)
                    val bytesRead = saveToFile(body, file)
                    Log.d("YtbDownloadRepository", "Downloaded $bytesRead bytes for file $filename")
                    Result.success(file)
                } else {
                    Result.failure(Exception("Corps de la réponse vide"))
                }
            } else {
                // Tenter de parser la réponse d'erreur
                val errorBody = response.errorBody()?.string()
                val errorResponse = if (errorBody != null) {
                    Gson().fromJson(errorBody, YtbDownloadErrorResponse::class.java)
                } else {
                    YtbDownloadErrorResponse(status = "error", message = "Erreur inconnue.")
                }
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Log.e("YtbDownloadRepository", "Exception during download", e)
            Result.failure(e)
        }
    }

    private fun saveToFile(body: ResponseBody, file: File): Long {
        var inputStream = body.byteStream()
        var outputStream: FileOutputStream? = null
        var totalBytesRead: Long = 0
        try {
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
                totalBytesRead += read
                Log.d("YtbDownloadRepository", "Read $read bytes, totalBytesRead: $totalBytesRead")
            }
            outputStream.flush()
            Log.d("YtbDownloadRepository", "File saved successfully: ${file.absolutePath}")
            return totalBytesRead
        } catch (e: Exception) {
            Log.e("YtbDownloadRepository", "Error saving file", e)
            throw e
        } finally {
            inputStream.close()
            outputStream?.close()
        }
    }


}

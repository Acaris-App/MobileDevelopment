package com.acaris.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = when {
                mimeType.contains("png", ignoreCase = true) -> ".png"
                mimeType.contains("webp", ignoreCase = true) -> ".webp"
                else -> ".jpg"
            }
            val tempFile = File(context.cacheDir, "profile_temp_${System.currentTimeMillis()}$extension")

            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
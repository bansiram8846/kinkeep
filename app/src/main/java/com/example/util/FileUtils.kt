package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    private const val TAG = "FileUtils"

    /**
     * Resolves the real display name of the selected file from any Android folder/provider.
     */
    fun getFileName(context: Context, uri: Uri): String {
        var name = ""
        val scheme = uri.scheme
        if (scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            name = cursor.getString(index) ?: ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to resolve file name: ${e.message}")
            }
        }
        if (name.isBlank()) {
            name = uri.lastPathSegment?.substringAfterLast('/') ?: "document_${System.currentTimeMillis()}"
        }
        return name
    }

    /**
     * Copies a file from any picked folder (Downloads, Documents, Storage, Drive) into
     * private internal app storage so that the URI never expires and full-screen view works anytime.
     */
    fun copyUriToVaultStorage(context: Context, sourceUri: Uri): File? {
        return try {
            val docsDir = File(context.filesDir, "vault_documents").apply { mkdirs() }
            val originalName = getFileName(context, sourceUri)
            val extension = originalName.substringAfterLast('.', "jpg")
            val sanitizedBase = originalName.substringBeforeLast('.').replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            val targetFile = File(docsDir, "${sanitizedBase}_${System.currentTimeMillis()}.$extension")

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            targetFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy file to vault storage: ${e.message}", e)
            null
        }
    }

    /**
     * Formats bytes into human-readable string (e.g. 1.2 MB, 450 KB).
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format(java.util.Locale.US, "%.1f MB", bytes.toDouble() / (1024 * 1024))
        }
    }
}

package com.isl.assetManagement.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import android.util.Base64


class ImageUtil {
    companion object {
        fun getFileNameFromUri(context: Context, uri: Uri, fileName: String): String {
            var finalFileName = fileName ?: "" // Assign a mutable variable

            if (finalFileName.isEmpty()) {
                finalFileName = "default_filename.jpg"
            }

            // Default name if extraction fails
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        finalFileName = it.getString(nameIndex) // Extract file name
                    }
                }
            }
            return fileName
        }

        fun convertImageUriToBase64(context: Context, imageUri: Uri): String? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)?.let { resizeBitmap(it, 800, 800) } // Resize to 800x800

                val outputStream = ByteArrayOutputStream()
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                } // Compress to reduce size
                val byteArray: ByteArray = outputStream.toByteArray()

                Base64.encodeToString(byteArray, Base64.NO_WRAP) // Encode as Base64
            } catch (e: Exception) {
                e.printStackTrace()
                null // Return null if conversion fails
            }
        }

        fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val aspectRatio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
            val width: Int
            val height: Int

            if (bitmap.width > bitmap.height) {
                width = maxWidth
                height = (maxWidth / aspectRatio).toInt()
            } else {
                height = maxHeight
                width = (maxHeight * aspectRatio).toInt()
            }

            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }

    }
}
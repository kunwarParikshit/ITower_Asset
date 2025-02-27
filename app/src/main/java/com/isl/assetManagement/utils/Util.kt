package com.isl.assetManagement.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class Util {
    companion object {

        fun convertDate1(requestDate: String, dateFormate: String): String {
            // Define the input format (dd/MM/yyyy)
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            // Parse the input date string into a LocalDate object
            val localDate = LocalDate.parse(requestDate, inputFormatter)

            // Convert the LocalDate to an Instant at the start of the day in UTC
            val instant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant()

            // Convert Instant to ZonedDateTime (UTC timezone)
            val zonedDateTime = instant.atZone(ZoneOffset.UTC)

            // Define the output format (dd-MM-yyyy HH:mm:ss)
            val outputFormatter = DateTimeFormatter.ofPattern(dateFormate)

            // Format the ZonedDateTime to the required format
            return zonedDateTime.format(outputFormatter)
        }

        fun convertDate(requestDate: String, dateFormate: String): String {
            // Parse the input date string to Instant
            val instant = Instant.parse(requestDate)

            // Convert Instant to ZonedDateTime (UTC timezone)
            val zonedDateTime = instant.atZone(ZoneOffset.UTC)

            // Define the output format (dd-MM-yyyy HH:mm:ss)
            val outputFormatter = DateTimeFormatter.ofPattern(dateFormate)

            // Format the ZonedDateTime to the required format
            return zonedDateTime.format(outputFormatter)
        }

        // Method to convert string to HashMap
        fun stringToHashMap(input: String): HashMap<String, String> {
            val hashMap = HashMap<String, String>()
            val keyValuePairs = input.split(",")

            for (pair in keyValuePairs) {
                val keyValue = pair.split("=")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim()
                    val value = keyValue[1].trim()
                    hashMap[key] = value
                }
            }
            return hashMap
        }

        fun getCurrentDateTime(): String {
            //val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm")
            val formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm a")
            return LocalDateTime.now().format(formatter)
        }

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

        fun convertImageUriToBase64Optimized(context: Context, imageUri: Uri): String? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?.let { resizeBitmap(it, 800, 800) } // Resize to 800x800

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

        fun isFirstDateGreater(firstDate: String, secondDate: String): Boolean {
            return try {
                val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                val date1 = LocalDate.parse(firstDate, formatter)
                val date2 = LocalDate.parse(secondDate, formatter)
                date1.isAfter(date2)
            } catch (e: Exception) {
                false
            }
        }

        object CameraUtils {

            private var imageUri: Uri? = null
            private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
            private const val REQUEST_CAMERA_PERMISSION = 1001
            private const val REQUEST_IMAGE_CAPTURE = 1002

            /**
             * Handles the capture button click, checks for permissions, and opens the camera.
             */
            fun handleCaptureButtonClick(activity: Activity) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        CAMERA_PERMISSION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera(activity)
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(CAMERA_PERMISSION),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
            }

            /**
             * Opens the camera and saves the image.
             */
            private fun openCamera(activity: Activity) {
                val photoFile = createImageFile(activity)
                imageUri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.provider",
                    photoFile
                )

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                }

                if (cameraIntent.resolveActivity(activity.packageManager) != null) {
                    activity.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
                } else {
                    Toast.makeText(activity, "No camera app found", Toast.LENGTH_SHORT).show()
                }
            }

            /**
             * Creates an image file for storing the captured image.
             */
            private fun createImageFile(context: Context): File {
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
            }

            /**
             * Handles the result of the camera capture and sets the image.
             */
            fun handleActivityResult(requestCode: Int, resultCode: Int, imageView: ImageView) {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                    imageUri?.let { uri ->
                        imageView.setImageURI(uri)
                    }
                }
            }

            /**
             * Handles permission result.
             */
            fun handlePermissionsResult(
                requestCode: Int,
                grantResults: IntArray,
                activity: Activity
            ) {
                if (requestCode == REQUEST_CAMERA_PERMISSION) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openCamera(activity)
                    } else {
                        Toast.makeText(
                            activity,
                            "Camera permission is required!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }
}
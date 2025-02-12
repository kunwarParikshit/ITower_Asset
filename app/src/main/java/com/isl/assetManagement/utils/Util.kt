package com.isl.assetManagement.utils
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Util {
    companion object {
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
    }
}
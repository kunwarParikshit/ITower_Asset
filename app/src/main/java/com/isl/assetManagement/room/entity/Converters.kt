package com.isl.assetManagement.room.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.responses.Location
import com.isl.assetManagement.responses.Timeline

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLocation(location: Location): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(json: String): Location {
        return gson.fromJson(json, Location::class.java)
    }

    @TypeConverter
    fun fromAssetList(assets: List<Assets>): String {
        return gson.toJson(assets)
    }

    @TypeConverter
    fun toAssetList(json: String): List<Assets> {
        val type = object : TypeToken<List<Assets>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromTimelineList(timelines: List<Timeline>): String {
        return gson.toJson(timelines)
    }

    @TypeConverter
    fun toTimelineList(json: String): List<Timeline> {
        val type = object : TypeToken<List<Timeline>>() {}.type
        return gson.fromJson(json, type)
    }
}

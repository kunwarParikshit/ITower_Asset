package com.isl.assetManagement.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class TaskDetailResponce(
    val requestId: String,
    val movementDate: String,
    val siteCategory: String,
    val requestDate: String,
    val reasonCategory: String,
    val reasonSubCategory: String,
    val status: String,
    val requestor: String,
    val fromLocation: Location,
    val toLocation: Location,
    val assets: List<Assets>,
    val documents: List<Documents>,
    val timelines: List<Timeline>,
)

data class Location(
    val siteId: String,
    val address: String,
    val locationLevel2: String,
    val locationLevel3: String,
    val locationLevel4: String,
    val latitude: Double,
    val longitude: Double
)

data class Documents(
    val userId: String = "",
    val fileName: String = "",
    val content: String = "",
    val latiude: Double ,
    val latitude: Double,
    val longitude: Double,
    val tagName: String = "",
    val timeStamp: String = "",
    val url: String = "",
    val type: String = "",
    val tempDocId: String = "",
    val localPath: String = "",
    val status: Int = 0  //0 means new upload images include in payload,
    // 1 means already uploaded no include in payload,
    // 2 means pending for server after upload to server then revert 0
)

@Parcelize
data class Assets(
    val assetType: String,
    val assetId: String? = null,
    val itemCode: String,
    val qrCode: String? = null,
    val availableQty: Int,
    val requestedQty: Int,
    val approvedQty: Int,
    val status: Int = 0,
    //val id: String
    var id: String? = UUID.randomUUID().toString()  // Make it nullable
): Parcelable

data class Timeline(
    val stage: String,
    val status: String,
    val user: String? = null,
    val timestamp: String,
    val latitude: Double,
    val longitude: Double
)


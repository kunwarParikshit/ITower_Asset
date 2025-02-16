package com.isl.assetManagement.requests

data class TaskUploadPayload(
    val woNumber: String = "",
    val project: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val reasonCategory: String = "",
    val reasonSubCategory: String = "",
    val movementDate: String = "",
    val remarks: String = "",
    val status : String = "",
    val source: String = "",
    val user: String = "",
    val assets: List<Asset> = emptyList(),
    val documents: List<Document> = emptyList()//,
    //val replacementDetail: ReplacementDetail = ReplacementDetail()
)

data class Asset(
    val assetId: String = "",
    val qrCode: String = ""
)

data class Document(
    val latiude: Double = 0.0,
    val longitude: Double = 0.0,
    val tagName: String = "",
    val timeStamp: String = "",
    val url: String = "",
    val type: String = "",
    val content: String = "",
    val tempDocId: String = ""
)

/*data class ReplacementDetail(
    val fromLocation: String = "",
    val reasonCategory: String = "",
    val reasonSubCategory: String = "",
    val movementDate: String = ""
)*/







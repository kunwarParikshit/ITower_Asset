package com.isl.assetManagement.responses

data class AssetRequestResponse(
    val requestId: String,  // Unique String identifier
    val fromLocation: String,
    val toLocation: String,
    val requestDate: String,
    val reasonCategory: String,
    val reasonSubCategory: String,
    val requestStatus: String,  // Status of the request (e.g., "Raised", "Awaiting")
    val totalAssetCount: Int
)
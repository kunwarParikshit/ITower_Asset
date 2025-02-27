package com.isl.assetManagement.responses

typealias SiteDetailResponse = List<SiteDetail>

data class SiteDetail(
    val siteId: String?,
    val siteName: String?,
    val siteCategory: String?,
    val latitude: Double?,
    val longitude: Double?,
    val opcoSiteId: String? // Optional field
)
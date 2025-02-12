package com.isl.assetManagement.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AssetDetailsResponse (
    var assetId: String? = " ",
    var assetType: String? = " ",
    var itemCode : String? = " ",
    var assetCriteria: String? = " ",
    var siteId: String? = " ",
    var siteAddress: String? = " ",
    var siteName: String? = " ",
    var qrCode: String? = " "
): Parcelable


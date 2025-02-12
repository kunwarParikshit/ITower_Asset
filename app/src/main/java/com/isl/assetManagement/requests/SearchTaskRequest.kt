package com.isl.assetManagement.requests

import android.os.Parcel
import android.os.Parcelable

data class SearchTaskRequest(
    val requestId: String,
    val requestStatus: String,
    val fromLocation: String,
    val toLocation: String,
    val fromDate: String,
    val toDate: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        requestId = parcel.readString() ?: "",
        requestStatus = parcel.readString() ?: "",
        fromLocation = parcel.readString() ?: "",
        toLocation = parcel.readString() ?: "",
        fromDate = parcel.readString() ?: "",
        toDate = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(requestId)
        parcel.writeString(requestStatus)
        parcel.writeString(fromLocation)
        parcel.writeString(toLocation)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<SearchTaskRequest> {
            override fun createFromParcel(parcel: Parcel): SearchTaskRequest {
                return SearchTaskRequest(parcel)
            }

            override fun newArray(size: Int): Array<SearchTaskRequest?> {
                return arrayOfNulls(size)
            }
        }
    }
}

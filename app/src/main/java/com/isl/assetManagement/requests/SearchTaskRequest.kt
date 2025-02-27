package com.isl.assetManagement.requests

import android.os.Parcel
import android.os.Parcelable


data class SearchTaskRequest(
    val requestId: String, // user will enter manually
    val requestStatus: String, //hardcoded strings for drop downs, no manual text
    val fromLocation: String,  // auto search site api location can not be same, atleast 4 chars
    val toLocation: String, // auto get only the one from api, not manual in search
    val fromDate: String,  // date picker 17/02/2025
    val toDate: String  // date picker from<=to
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

package com.isl.assetManagement.dataViewModel

import FetchDeviceIDRequest
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.assetManagement.responses.AssetDetailsResponse
import com.isl.assetManagement.responses.FetchUserIdResponse
import com.isl.assetManagement.responses.SingleMessageResponse
import com.isl.assetManagement.room.repository.RemoteRepository
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteViewModel (private val repository: RemoteRepository) : ViewModel() {

    fun getUserId(
        successCallback: (FetchUserIdResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        body: FetchDeviceIDRequest,
        token : String
    ) {
        viewModelScope.launch {
            repository.fetchDeviceID(successCallback, errorCallback, body = body,token)
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun getAssetDetails(
        token: String,
        siteId: String,
        assetId: String?,
        qrCode: String?,
        onDataInserted: (Int) -> Unit,
        resultCallback: (List<AssetDetailsResponse>?) -> Unit
    ) /*{
        viewModelScope.launch {
            val assetDetails = repository.fetchAssetDetails(token, siteId, assetId, qrCode, onDataInserted)
            resultCallback(assetDetails)
        }
    }*/
    {
        //viewModelScope.launch {
            //val assetDetails = repository.fetchAssetDetails(token, siteId, assetId, qrCode, onDataInserted)
            //resultCallback(assetDetails)
        //}
        val hardcodedList = listOf(
            AssetDetailsResponse(
                assetId = "TG-000010051",
                assetType = "Rectifier Module",
                itemCode = "RM0001",
                assetCriteria = "Non_Consumable",
                siteId = "IZ100003",
                siteAddress = "Site Address 111",
                siteName = "Site Name 1111",
                qrCode = "QWERT23"
            )
        )
            resultCallback(hardcodedList )


    }

}

package com.isl.assetManagement.room.repository

import FetchDeviceIDRequest
import android.util.Log
import com.isl.assetManagement.api.ApiClient
import com.isl.assetManagement.api.Client
import com.isl.assetManagement.responses.AssetDetailsResponse
import com.isl.assetManagement.responses.FetchUserIdResponse
import com.isl.assetManagement.responses.SingleMessageResponse
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteRepository {
    private val api = Client.request

    fun fetchDeviceID(
        callback: (FetchUserIdResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        body: FetchDeviceIDRequest,
        token: String
    ) {
        val authHeader = "Bearer $token"
        val observable: Observable<FetchUserIdResponse> = api!!.fetchDeviceID( body = body,authHeader = authHeader)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FetchUserIdResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: FetchUserIdResponse) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    if (e is NullPointerException) {
                        errorCallBack(SingleMessageResponse("NullPointerException: $e"))
                    } else {
                        errorCallBack(SingleMessageResponse(e.message ?: "Unknown error"))
                    }
                }

                override fun onComplete() {
                }
       })
    }


    suspend fun fetchAssetDetails(
        token: String,
        siteId: String,
        assetId: String?,
        qrCode: String?,
        onDataInserted: (Int) -> Unit
    ): List<AssetDetailsResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val userId = KotlinPrefkeeper.assetUserId ?: return@withContext null

                // Use safe call here to avoid invoking on a null object
                val response = api?.fetchAssetDetails(
                    authHeader = authHeader,
                    userId = userId,
                    siteId = siteId,
                    assetId = assetId,
                    qrCode = qrCode
                )

                if (response?.isSuccessful == true) {
                    val data = response.body()
                    if (!data.isNullOrEmpty()) {
                        onDataInserted(1)
                    return@withContext data
                    } else {
                        onDataInserted(0)
                        Log.e("API_ERROR", "Response body is null or empty")
                        return@withContext null
                    }
                } else {
                    onDataInserted(0)
                    Log.e("API_ERROR", "API Error: ${response?.errorBody()?.string()}")
                    return@withContext null
                }
            } catch (e: Exception) {
                onDataInserted(0)
                Log.e("API_ERROR", "Exception: ${e.message}")
                return@withContext null
            }
        }
    }






}


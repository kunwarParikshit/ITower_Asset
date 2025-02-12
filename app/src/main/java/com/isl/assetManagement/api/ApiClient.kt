package com.isl.assetManagement.api

import com.isl.assetManagement.constants.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    //private const val BASE_URL = "http://192.168.0.35:6007/"
    // Retrofit instance setup for general API requests (ONM)
    private val retrofitOnm by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.onmUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api_onm: IApiRequest by lazy {
        retrofitOnm.create(IApiRequest::class.java)
    }

    //private const val ASSET_AUTH_TOKEN_URL = "https://idpdev-app.infozech.com:9014/"

    // Retrofit instance setup for Auth requests, with insecure HTTP client
    private val auth by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.tokenUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
       // .client(getInsecureHttpClient())
    // Retrofit service to make token requests
    val authService: IApiRequest by lazy {
        auth.create(IApiRequest::class.java)
    }

    //private const val ASSET_URL = "https://midc-qc-iasset.infozech.com/"
    //private const val ASSET_URL = "https://midc-qc-iasset.infozech.com/"

    // Retrofit instance setup for Asset-related requests
    private val retrofitAsset by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api_asset: IApiRequest by lazy {
        retrofitAsset.create(IApiRequest::class.java)
    }
}

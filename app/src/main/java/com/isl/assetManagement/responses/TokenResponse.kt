package com.isl.assetManagement.responses

data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val refresh_token: String,
    val token_type: String,
    val `not-before-policy`: Long,
    val session_state: String,
    val scope: String
)
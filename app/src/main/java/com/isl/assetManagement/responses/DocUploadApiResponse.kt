package com.isl.assetManagement.responses

// Success Response
data class SuccessResponse(
    val docId: Int,
    val size: String
)

// Error Response
data class ErrorDetail(
    val code: String,
    val message: String
)

data class ErrorResponse(
    val flag: String,
    val errors: List<ErrorDetail>
)

// Sealed class to handle both cases
sealed class DocUploadApiResponse {
    data class Success(val data: SuccessResponse) : DocUploadApiResponse()
    data class Error(val error: ErrorResponse) : DocUploadApiResponse()
}


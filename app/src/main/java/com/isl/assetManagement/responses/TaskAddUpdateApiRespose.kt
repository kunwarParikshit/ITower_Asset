package com.isl.assetManagement.responses

data class TaskAddUpdateApiRespose(
    val flag: String,
    val message: String,
    val data: Data?,
    val errors: List<ErrorDetail> = emptyList()
)

data class Data(
    val id: String,
    val replaceId: String
)

/*data class ErrorDetail(
    val code: String,
    val message: String,
    val fieldName: String
)*/


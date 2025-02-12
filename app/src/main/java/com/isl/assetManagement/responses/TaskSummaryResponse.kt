package com.isl.assetManagement.responses

data class TaskSummaryResponse(
    val assigned: Int,
    val raised: Int,
    val completed: Int,
    val rejected: Int
)


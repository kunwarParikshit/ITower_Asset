package com.isl.assetManagement.responses

data class ParamTypeResponse(
    val paramType: String,
    val paramId: String,
    val paramValue: String,
    val localValue: String,
    val parentId: String
)
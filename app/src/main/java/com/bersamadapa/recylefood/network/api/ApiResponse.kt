package com.bersamadapa.recylefood.network.api

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T
)
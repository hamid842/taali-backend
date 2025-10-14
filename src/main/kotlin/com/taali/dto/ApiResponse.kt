package com.taali.dto

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
) {
    companion object {
        fun success(message: String): ApiResponse {
            return ApiResponse(true, message)
        }

        fun success(message: String, data: Any?): ApiResponse {
            return ApiResponse(true, message, data)
        }

        fun error(message: String): ApiResponse {
            return ApiResponse(false, message)
        }
    }
}
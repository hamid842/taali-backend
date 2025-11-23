package com.taali.api.dto.message.request

data class RegisterDeviceRequest(
    val expoPushToken: String,
    val deviceType: String? = null
)
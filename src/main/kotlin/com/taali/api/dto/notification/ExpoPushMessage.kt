package com.taali.api.dto.notification

data class ExpoPushMessage(
    val to: String,
    val title: String? = null,
    val body: String,
    val data: Map<String, Any?>? = null,
    val sound: String = "default",
    val badge: Int? = null
)

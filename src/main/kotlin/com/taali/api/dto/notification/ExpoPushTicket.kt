package com.taali.api.dto.notification

data class ExpoPushTicket(
    val id: String? = null,
    val status: String,
    val message: String? = null,
    val details: Map<String, Any?>? = null
)

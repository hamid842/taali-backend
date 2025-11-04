package com.taali.api.dto.school.request

data class UpdateCanteenRequest(
    val name: String? = null,
    val location: String? = null,
    val operatorUserId: Long? = null
)
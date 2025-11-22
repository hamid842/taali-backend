package com.taali.api.dto.parent.request

data class CreateParentRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val occupation: String? = null,
    val address: String? = null,
    val schoolId: Long? = null
)
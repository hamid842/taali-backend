package com.taali.api.dto.school.request

data class NewParentRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val occupation: String? = null,
    val address: String? = null
)

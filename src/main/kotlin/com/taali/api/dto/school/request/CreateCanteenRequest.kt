package com.taali.api.dto.school.request

import jakarta.validation.constraints.NotBlank

data class CreateCanteenRequest(
    @field:NotBlank val name: String,
    val location: String? = null,
    val schoolId: Long,
    val operatorUserId: Long? = null
)
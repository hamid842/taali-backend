package com.taali.api.dto.school

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ParentDto(
    val id: Long? = null,
    val userId: String? = null, // linked user
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:Email val email: String? = null,
    val phone: String? = null,
    val occupation: String? = null,
    val address: String? = null
)
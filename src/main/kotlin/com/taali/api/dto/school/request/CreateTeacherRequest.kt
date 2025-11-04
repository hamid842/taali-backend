package com.taali.api.dto.school.request

import jakarta.validation.constraints.NotBlank

data class CreateTeacherRequest(
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val specialization: String? = null,
    val qualification: String? = null,
    val schoolId: Long,
    val classIds: List<Long> = emptyList()  // Optional: assign teacher to classes
)
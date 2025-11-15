package com.taali.api.dto.school.response

data class StudentAssignmentResponse(
    val studentId: Long,
    val success: Boolean,
    val error: String?
)

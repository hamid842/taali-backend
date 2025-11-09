package com.taali.api.dto.school

data class StudentSummary(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val studentCode: String?
)

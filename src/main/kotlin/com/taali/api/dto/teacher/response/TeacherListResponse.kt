package com.taali.api.dto.teacher.response

data class TeacherListResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profileImage: String?,
    val isActive: Boolean,
    val subjects: List<String>,
    val classCount: Int
)
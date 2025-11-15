package com.taali.api.dto.teacher.response

data class TeacherDetailResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profileImage: String?,
    val isActive: Boolean,
    val qualification: String?,
    val experienceYears: Int?,
    val specializations: List<String>,
    val classes: List<TeacherClassResponse>
)
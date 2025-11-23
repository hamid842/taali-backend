package com.taali.api.dto.teacher

import com.taali.domain.enum.UserRole

data class TeacherDto(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val subject: String? = null,
    val role: UserRole? = null,
    val email: String? = null,
    val phone: String? = null,
    val profileImage: String? = null
)
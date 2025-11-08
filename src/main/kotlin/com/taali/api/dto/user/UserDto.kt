package com.taali.api.dto.user

import com.taali.api.dto.school.SchoolInfoDto
import com.taali.domain.enum.UserRole
import com.taali.domain.enum.UserStatus
import java.time.LocalDateTime
import java.util.UUID

data class UserDto(
    val id: Long?,
    val userId: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
    val status: UserStatus,
    val profileImage: String?,
    val school: SchoolInfoDto?,
    val lastLogin: LocalDateTime?,
    val isActive: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)




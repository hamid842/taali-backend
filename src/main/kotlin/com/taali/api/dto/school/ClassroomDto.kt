package com.taali.api.dto.school

import java.time.LocalDateTime

data class ClassroomDto(
    val id: Long,
    val name: String,
    val code: String,
    val grade: String?,
    val capacity: Int?,
    val schoolId: Long,
    val teacherId: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateClassroomRequest(
    val name: String,
    val code: String,
    val grade: String? = null,
    val capacity: Int? = null,
    val schoolId: Long,
    val teacherId: Long? = null
)

data class UpdateClassroomRequest(
    val name: String? = null,
    val code: String? = null,
    val grade: String? = null,
    val capacity: Int? = null,
    val teacherId: Long? = null
)

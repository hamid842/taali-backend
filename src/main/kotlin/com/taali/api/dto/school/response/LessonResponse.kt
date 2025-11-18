package com.taali.api.dto.school.response

import java.time.LocalDateTime

data class LessonResponse(
    val id: Long,
    val name: String,
    val nameEn: String?,
    val gradeLevel: String,
    val color: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

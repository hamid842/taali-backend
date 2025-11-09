package com.taali.api.dto.school.response

data class LessonResponse(
    val id: Long,
    val name: String,
    val nameEn: String?,
    val gradeLevel: String,
    val color: String?
)

package com.taali.api.dto.teacher

import java.time.LocalDateTime

data class TeacherActivity(
    val id: Long,
    val type: String, // "attendance", "grading", "assignment", "message"
    val title: String,
    val description: String,
    val timestamp: LocalDateTime,
    val classId: Long?
)

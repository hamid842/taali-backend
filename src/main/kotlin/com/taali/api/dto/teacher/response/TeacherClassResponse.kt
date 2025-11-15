package com.taali.api.dto.teacher.response

data class TeacherClassResponse(
    val classId: Long,
    val className: String,
    val subject: String?,
    val isMainTeacher: Boolean,
    val studentCount: Long
)
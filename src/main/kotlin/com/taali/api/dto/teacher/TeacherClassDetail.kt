package com.taali.api.dto.teacher

data class TeacherClassDetail(
    val id: Long,
    val className: String,
    val subject: String,
    val gradeLevel: String,
    val studentCount: Int,
    val schedule: String,
    val room: String
)

package com.taali.api.dto.school.response

import com.taali.api.dto.school.SchoolSummary
import com.taali.api.dto.school.TeacherSummary
import java.time.LocalDateTime

data class SchoolClassResponse(
    val id: Long,
    val name: String,
    val gradeLevel: String?,
    val academicYear: String,
    val capacity: Int,
    val isActive: Boolean,
    val school: SchoolSummary?,
    val mainTeacher: TeacherSummary?,
    val studentCount: Int,
    val teacherCount: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)





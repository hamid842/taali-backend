package com.taali.api.dto.school.request

data class CreateSchoolClassRequest(
    val name: String,
    val gradeLevel: String?,
    val academicYear: String,
    val capacity: Int = 30,
    val schoolId: Long,
    val mainTeacherId: Long?,
    val studentIds: List<Long> = emptyList(),
    val teacherIds: List<Long> = emptyList()
)

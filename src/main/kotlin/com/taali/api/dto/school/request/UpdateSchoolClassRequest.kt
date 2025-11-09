package com.taali.api.dto.school.request

data class UpdateSchoolClassRequest(
    val name: String?,
    val gradeLevel: String?,
    val academicYear: String?,
    val capacity: Int?,
    val isActive: Boolean?,
    val mainTeacherId: Long?,
    val studentIds: List<Long>?,
    val teacherIds: List<Long>?
)

package com.taali.api.dto.parent.response

data class ChildDetailResponse(
    val id: Long,
    val name: String,
    val grade: String,
    val className: String,
    val schoolName: String,
    val attendanceRate: Int,
    val averageGrade: String?,
    val teacherName: String,
    val profileImage: String?,
    val birthDate: String?,
    val emergencyContact: String?,
    val medicalNotes: String?,
    val enrollmentDate: String
)

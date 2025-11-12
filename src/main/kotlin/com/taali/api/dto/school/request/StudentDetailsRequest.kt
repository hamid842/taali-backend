package com.taali.api.dto.school.request

import java.time.LocalDate

data class StudentDetailsRequest(
    val studentId: String? = null,
    val idNumber: String? = null,
    val birthDate: LocalDate? = null,
    val gender: String? = null,
    val gradeLevel: String? = null,
    val emergencyContact: String? = null,
    val emergencyPhone: String? = null,
    val medicalNotes: String? = null,
    val classId: Long? = null
)

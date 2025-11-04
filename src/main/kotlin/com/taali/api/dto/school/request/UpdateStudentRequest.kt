package com.taali.api.dto.school.request

import com.taali.domain.enum.Gender
import java.time.LocalDate

data class UpdateStudentRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val studentId: String? = null,
    val idNumber: String? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender? = null,
    val schoolClassId: Long? = null,
    val emergencyContact: String? = null,
    val emergencyPhone: String? = null,
    val medicalNotes: String? = null
)
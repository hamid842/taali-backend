package com.taali.api.dto.school.request

import com.taali.api.dto.school.ParentDto
import com.taali.domain.enum.Gender
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class CreateStudentRequest(
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    val studentId: String? = null,
    val idNumber: String? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender? = null,
    val schoolClassId: Long? = null,  // Assigned class
    val parents: List<ParentDto> = emptyList(),  // Nested parent info
    val emergencyContact: String? = null,
    val emergencyPhone: String? = null,
    val medicalNotes: String? = null
)


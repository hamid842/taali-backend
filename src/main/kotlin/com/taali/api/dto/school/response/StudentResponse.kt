package com.taali.api.dto.school.response

import com.taali.domain.enum.Gender

data class StudentResponse(
    val id: Long?,
    val studentId: String?,
    val idNumber: String?,
    val birthDate: String?,
    val gender: Gender?,
    val gradeLevel: String?,
    val emergencyContact: String?,
    val emergencyPhone: String?,
    val medicalNotes: String?,
    val classId: Long?,
    val className: String?,
    // Only include necessary user fields, not the entire User entity
    val userId: Long?,
    val userFirstName: String?,
    val userLastName: String?,
    val userEmail: String?,
    val isActive: Boolean?,
    val profileImageUrl: String?,
    val age: Int?
)

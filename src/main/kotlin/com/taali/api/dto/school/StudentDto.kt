package com.taali.api.dto.school

import com.taali.api.dto.user.UserDto
import com.taali.domain.model.school.Student

data class StudentDTO(
    val id: Long?,
    val name: String,
    val user: UserDto
) {
    companion object {
        fun fromEntity(student: Student): StudentDTO {
            return StudentDTO(
                id = student.user?.id,
                name = student.getFullName(),
                user = UserDto.fromEntity(student.user)
            )
        }
    }
}
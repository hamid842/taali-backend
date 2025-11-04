package com.taali.api.mapper

import com.taali.api.dto.school.SchoolDto
import com.taali.domain.model.school.School

fun School.toDto(): SchoolDto {
    return SchoolDto(
        id = this.id!!,
        name = this.name,
        code = this.code,
        image = this.image,
        address = this.address,
        email = this.email,
        phone = this.phone,
        ownerId = this.ownerId,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        teacherCount = this.teachers.size.toLong(),
        classCount = this.classes.size.toLong(),
        studentCount = this.students.size.toLong(),
        canteenCount = this.canteens.size.toLong()
    )
}

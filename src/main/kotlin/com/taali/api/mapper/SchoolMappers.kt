package com.taali.api.mapper

import com.taali.api.dto.school.SchoolDto
import com.taali.domain.model.school.School

fun School.toDto(): SchoolDto {
    return SchoolDto(
        id = this.id ?: 0,
        name = this.name,
        code = this.code,
        image = this.image,
        address = this.address,
        email = this.email,
        phone = this.phone,
        ownerId = this.owner?.id,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        teacherCount = this.teacherCount,
        classCount = this.classCount,
        studentCount = this.studentCount,
        schoolType = this.schoolType,
        shiftType = this.shiftType,
        educationalLevel = this.educationalLevel,
        studentsCapacity = this.studentsCapacity,
        studentCapacityPercentage = this.studentCapacityPercentage,
        hasAvailableSeats = this.hasAvailableSeats,
        availableSeats = this.availableSeats,
        website = this.website,
        establishedYear = this.establishedYear,
        motto = this.motto,
        schoolAge = this.schoolAge,
        totalClassrooms = this.totalClassrooms,
        totalLabs = this.totalLabs,
        hasTransportFacility = this.hasTransportFacility,
        hasHostelFacility = this.hasHostelFacility,
        hasCafeteria = this.hasCafeteria,
        hasLibrary = this.hasLibrary,
        hasSportsFacility = this.hasSportsFacility,
        annualTuitionFee = this.annualTuitionFee,
        accreditation = this.accreditation,
        tags = this.tags
    )
}

package com.taali.api.mapper

import com.taali.api.dto.school.SchoolDto
import com.taali.domain.model.school.School

fun School.toDto(): SchoolDto {
    // Make sure ISCED levels are mapped from educational levels
    mapEducationalLevelsToIsced()

    return SchoolDto(
        id = this.id ?: 0L,
        name = this.name,
        code = this.code,
        image = this.image,
        address = this.address,
        email = this.email,
        phone = this.phone,
        ownerId = this.owner?.id,
        status = this.status,
        schoolType = this.schoolType,
        shiftType = this.shiftType,
        educationalLevels = this.educationalLevels,
        iscedLevels = this.iscedLevels,
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
        hasTransportFacility = this.hasTransportFacilitySafe,
        hasHostelFacility = this.hasHostelFacilitySafe,
        hasCafeteria = this.hasCafeteriaSafe,
        hasLibrary = this.hasLibrarySafe,
        hasSportsFacility = this.hasSportsFacilitySafe,
        annualTuitionFee = this.annualTuitionFee,
        accreditation = this.accreditation,
        tags = this.tags,
        teacherCount = this.teacherCount,
        classCount = this.classCount,
        studentCount = this.studentCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

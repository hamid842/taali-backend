package com.taali.api.dto.school

import com.taali.domain.enum.EducationalLevel
import com.taali.domain.enum.IscedLevel
import com.taali.domain.enum.SchoolStatus
import com.taali.domain.enum.SchoolType
import com.taali.domain.enum.ShiftType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

// Main School DTO
data class SchoolDto(
    val id: Long,
    val name: String,
    val code: String,
    val image: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val ownerId: Long? = null,
    val status: SchoolStatus = SchoolStatus.ACTIVE,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    // Counts
    val teacherCount: Long = 0,
    val classCount: Long = 0,
    val studentCount: Long = 0,

    // Classification
    val schoolType: SchoolType? = null,
    val shiftType: ShiftType? = null,
    val educationalLevels: Set<EducationalLevel> = emptySet(),
    val iscedLevels: Set<IscedLevel> = emptySet(),

    // Capacity
    val studentsCapacity: Int = 0,
    val studentCapacityPercentage: Double = 0.0,
    val hasAvailableSeats: Boolean = false,
    val availableSeats: Int = 0,

    // Additional info
    val website: String? = null,
    val establishedYear: Int? = null,
    val motto: String? = null,
    val schoolAge: Int? = null,

    // Infrastructure
    val totalClassrooms: Int = 0,
    val totalLabs: Int = 0,
    val hasTransportFacility: Boolean = false,
    val hasHostelFacility: Boolean = false,
    val hasCafeteria: Boolean = false,
    val hasLibrary: Boolean = false,
    val hasSportsFacility: Boolean = false,

    // Financial & accreditation
    val annualTuitionFee: BigDecimal? = null,
    val accreditation: String? = null,

    // Tags
    val tags: Set<String> = emptySet()
)

// CreateSchoolRequest DTO
data class CreateSchoolRequest(
    @field:NotBlank @field:Size(max = 255)
    val name: String,

    @field:NotBlank @field:Size(max = 50)
    @field:Pattern(regexp = "^[A-Z0-9_-]+$")
    val code: String,

    @field:PositiveOrZero
    val ownerId: Long? = null,

    val image: String? = null,
    @field:Size(max = 20)
    val phone: String? = null,
    @field:Email @field:Size(max = 255)
    val email: String? = null,
    val address: String? = null,

    val schoolType: SchoolType? = null,
    val shiftType: ShiftType? = null,
    val educationalLevels: Set<EducationalLevel> = emptySet(),

    @field:PositiveOrZero
    val studentsCapacity: Int = 0,

    @field:Min(1800) @field:Max(2100)
    val establishedYear: Int? = null,

    @field:Size(max = 500)
    val website: String? = null,

    @field:Size(max = 500)
    val motto: String? = null,

    @field:PositiveOrZero
    val totalClassrooms: Int = 0,

    @field:PositiveOrZero
    val totalLabs: Int = 0,

    val hasTransportFacility: Boolean = false,
    val hasHostelFacility: Boolean = false,
    val hasCafeteria: Boolean = false,
    val hasLibrary: Boolean = false,
    val hasSportsFacility: Boolean = false,

    val annualTuitionFee: BigDecimal? = null,
    @field:Size(max = 255)
    val accreditation: String? = null,
    val status: SchoolStatus = SchoolStatus.ACTIVE,
    val tags: Set<String> = emptySet()
)

// UpdateSchoolRequest DTO
data class UpdateSchoolRequest(
    @field:Size(max = 255) val name: String? = null,
    @field:Size(max = 50) @field:Pattern(regexp = "^[A-Z0-9_-]*$") val code: String? = null,
    val image: String? = null,
    val address: String? = null,
    @field:Email @field:Size(max = 255) val email: String? = null,
    @field:Size(max = 20) val phone: String? = null,
    val schoolType: SchoolType? = null,
    val shiftType: ShiftType? = null,
    val educationalLevels: Set<EducationalLevel>? = null,
    @field:PositiveOrZero val studentsCapacity: Int? = null,
    @field:Min(1800) @field:Max(2100) val establishedYear: Int? = null,
    @field:Size(max = 500) val website: String? = null,
    @field:Size(max = 500) val motto: String? = null,
    @field:PositiveOrZero val totalClassrooms: Int? = null,
    @field:PositiveOrZero val totalLabs: Int? = null,
    val hasTransportFacility: Boolean? = null,
    val hasHostelFacility: Boolean? = null,
    val hasCafeteria: Boolean? = null,
    val hasLibrary: Boolean? = null,
    val hasSportsFacility: Boolean? = null,
    val annualTuitionFee: BigDecimal? = null,
    @field:Size(max = 255) val accreditation: String? = null,
    val status: SchoolStatus? = null,
    val tags: Set<String>? = null
)

// Summary DTO
data class SchoolSummaryDto(
    val id: Long,
    val name: String,
    val code: String,
    val image: String? = null,
    val schoolType: SchoolType? = null,
    val educationalLevels: Set<EducationalLevel> = emptySet(),
    val iscedLevels: Set<IscedLevel> = emptySet(),
    val studentCount: Long = 0,
    val teacherCount: Long = 0,
    val studentCapacityPercentage: Double = 0.0,
    val hasAvailableSeats: Boolean = false,
    val status: SchoolStatus = SchoolStatus.ACTIVE
)

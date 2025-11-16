package com.taali.api.dto.school

import com.taali.domain.enum.EducationalLevel
import com.taali.domain.enum.SchoolStatus
import com.taali.domain.enum.SchoolType
import com.taali.domain.enum.ShiftType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime

data class SchoolDto(
    val id: Long,
    val name: String,
    val code: String,
    val image: String?,
    val address: String?,
    val email: String?,
    val phone: String?,
    val ownerId: Long?,
    val status: SchoolStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    // Count fields
    val teacherCount: Long? = 0,
    val classCount: Long? = 0,
    val studentCount: Long? = 0,

    // New classification fields
    val schoolType: SchoolType?,
    val shiftType: ShiftType?,
    val educationalLevel: EducationalLevel?,

    // Capacity fields
    val studentsCapacity: Int? = 0,
    val studentCapacityPercentage: Double?,
    val hasAvailableSeats: Boolean?,
    val availableSeats: Int? = 0,

    // Additional info fields
    val website: String?,
    val establishedYear: Int?,
    val motto: String?,
    val schoolAge: Int?,

    // Infrastructure fields
    val totalClassrooms: Int? = 0,
    val totalLabs: Int? = 0,
    val hasTransportFacility: Boolean? = false,
    val hasHostelFacility: Boolean? = false,
    val hasCafeteria: Boolean? = false,
    val hasLibrary: Boolean? = false,
    val hasSportsFacility: Boolean? = false,

    // Financial & accreditation
    val annualTuitionFee: BigDecimal?,
    val accreditation: String?,

    // Tags for searchability
    val tags: Set<String>? = emptySet()
)

data class CreateSchoolRequest(
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    val name: String,

    @field:NotBlank(message = "School code is required")
    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    @field:Pattern(regexp = "^[A-Z0-9_-]+$", message = "School code can only contain uppercase letters, numbers, hyphens, and underscores")
    val code: String,

    @field:PositiveOrZero(message = "Owner ID must be positive")
    val ownerId: Long? = null,

    // Basic contact info
    val image: String? = null,

    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val phone: String? = null,

    @field:Email(message = "Please provide a valid email address")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String? = null,

    val address: String? = null,

    // Classification
    val schoolType: SchoolType? = null,
    val shiftType: ShiftType? = null,
    val educationalLevel: EducationalLevel? = null,

    // Capacity & establishment
    @field:PositiveOrZero(message = "Student capacity must be positive or zero")
    val studentsCapacity: Int? = 0,

    @field:Min(value = 1800, message = "Established year must be realistic")
    @field:Max(value = 2100, message = "Established year must be realistic")
    val establishedYear: Int? = null,

    // Additional info
    @field:Size(max = 500, message = "Website URL must not exceed 500 characters")
    val website: String? = null,

    @field:Size(max = 500, message = "Motto must not exceed 500 characters")
    val motto: String? = null,

    // Infrastructure
    @field:PositiveOrZero(message = "Total classrooms must be positive or zero")
    val totalClassrooms: Int = 0,

    @field:PositiveOrZero(message = "Total labs must be positive or zero")
    val totalLabs: Int? = 0,

    val hasTransportFacility: Boolean? = false,
    val hasHostelFacility: Boolean? = false,
    val hasCafeteria: Boolean? = false,
    val hasLibrary: Boolean? = false,
    val hasSportsFacility: Boolean? = false,

    // Financial
    val annualTuitionFee: BigDecimal? = null,

    // Accreditation
    @field:Size(max = 255, message = "Accreditation must not exceed 255 characters")
    val accreditation: String? = null,

    val status: SchoolStatus? = SchoolStatus.ACTIVE,
    val tags: Set<String>? = emptySet()
)

data class UpdateSchoolRequest(
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    val name: String? = null,

    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    @field:Pattern(regexp = "^[A-Z0-9_-]*$", message = "School code can only contain uppercase letters, numbers, hyphens, and underscores")
    val code: String? = null,

    // Contact info
    val image: String? = null,
    val address: String? = null,

    @field:Email(message = "Please provide a valid email address")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String? = null,

    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val phone: String? = null,

    // Classification
    val schoolType: SchoolType? = null,
    val shiftType: ShiftType? = null,
    val educationalLevel: EducationalLevel? = null,

    // Capacity & establishment
    @field:PositiveOrZero(message = "Student capacity must be positive or zero")
    val studentsCapacity: Int? = null,

    @field:Min(value = 1800, message = "Established year must be realistic")
    @field:Max(value = 2100, message = "Established year must be realistic")
    val establishedYear: Int? = null,

    // Additional info
    @field:Size(max = 500, message = "Website URL must not exceed 500 characters")
    val website: String? = null,

    @field:Size(max = 500, message = "Motto must not exceed 500 characters")
    val motto: String? = null,

    // Infrastructure
    @field:PositiveOrZero(message = "Total classrooms must be positive or zero")
    val totalClassrooms: Int? = null,

    @field:PositiveOrZero(message = "Total labs must be positive or zero")
    val totalLabs: Int? = null,

    val hasTransportFacility: Boolean? = null,
    val hasHostelFacility: Boolean? = null,
    val hasCafeteria: Boolean? = null,
    val hasLibrary: Boolean? = null,
    val hasSportsFacility: Boolean? = null,

    // Financial
    val annualTuitionFee: BigDecimal? = null,

    // Accreditation
    @field:Size(max = 255, message = "Accreditation must not exceed 255 characters")
    val accreditation: String? = null,

    val status: SchoolStatus? = null,
    val tags: Set<String>? = null
)

// Additional DTOs for specific use cases
data class SchoolSummaryDto(
    val id: Long,
    val name: String,
    val code: String,
    val image: String?,
    val schoolType: SchoolType?,
    val educationalLevel: EducationalLevel?,
    val studentCount: Long,
    val teacherCount: Long,
    val studentCapacityPercentage: Double,
    val hasAvailableSeats: Boolean,
    val status: SchoolStatus
)

data class SchoolStatsDto(
    val id: Long,
    val name: String,
    val totalStudents: Long,
    val totalTeachers: Long,
    val totalClasses: Long,
    val capacityUtilization: Double,
    val availableSeats: Int,
    val infrastructure: InfrastructureStatsDto
)

data class InfrastructureStatsDto(
    val totalClassrooms: Int,
    val totalLabs: Int,
    val hasTransport: Boolean,
    val hasHostel: Boolean,
    val hasCafeteria: Boolean,
    val hasLibrary: Boolean,
    val hasSports: Boolean
)


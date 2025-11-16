package com.taali.domain.model.school

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taali.domain.enum.SchoolStatus
import com.taali.domain.enum.SchoolType
import com.taali.domain.enum.ShiftType
import com.taali.domain.enum.EducationalLevel
import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.PositiveOrZero
import java.time.Year
import java.math.BigDecimal
import kotlin.math.max

@Entity
@Table(
    name = "schools",
    indexes = [
        Index(name = "idx_school_status", columnList = "status"),
        Index(name = "idx_school_type", columnList = "school_type"),
        Index(name = "idx_school_level", columnList = "educational_level"),
        Index(name = "idx_school_owner", columnList = "owner_id")
    ]
)
class School : AuditableEntity() {

    @Column(name = "name", nullable = false, length = 255)
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    var name: String = ""

    @Column(name = "code", unique = true, nullable = false, length = 50)
    @field:NotBlank(message = "School code is required")
    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    var code: String = ""

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "address", columnDefinition = "TEXT")
    var address: String? = null

    @Column(name = "email", length = 255)
    @field:Email(message = "Please provide a valid email address")
    var email: String? = null

    @Column(name = "phone", length = 20)
    var phone: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    var owner: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SchoolStatus = SchoolStatus.ACTIVE

    // New fields
    @Enumerated(EnumType.STRING)
    @Column(name = "school_type", length = 20)
    var schoolType: SchoolType? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", length = 20)
    var shiftType: ShiftType? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "educational_level", length = 20)
    var educationalLevel: EducationalLevel? = null

    @Column(name = "website", length = 500)
    var website: String? = null

    @Column(name = "students_capacity")
    @field:PositiveOrZero(message = "Student capacity must be positive or zero")
    var studentsCapacity: Int = 0

    @Column(name = "established_year")
    @field:Min(value = 1800, message = "Established year must be realistic")
    @field:Max(value = 2100, message = "Established year must be realistic")
    var establishedYear: Int? = null

    @Column(name = "motto", length = 500)
    var motto: String? = null

    @Column(name = "total_classrooms")
    @field:PositiveOrZero(message = "Total classrooms must be positive or zero")
    var totalClassrooms: Int = 0

    @Column(name = "total_labs")
    @field:PositiveOrZero(message = "Total labs must be positive or zero")
    var totalLabs: Int = 0

    @Column(name = "has_transport_facility")
    var hasTransportFacility: Boolean = false

    @Column(name = "has_hostel_facility")
    var hasHostelFacility: Boolean = false

    @Column(name = "has_cafeteria")
    var hasCafeteria: Boolean = false

    @Column(name = "has_library")
    var hasLibrary: Boolean = false

    @Column(name = "has_sports_facility")
    var hasSportsFacility: Boolean = false

    @Column(name = "annual_tuition_fee", precision = 10, scale = 2)
    var annualTuitionFee: BigDecimal? = null

    @Column(name = "accreditation", length = 255)
    var accreditation: String? = null

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "school_tags", joinColumns = [JoinColumn(name = "school_id")])
    @Column(name = "tag")
    var tags: Set<String> = mutableSetOf()

    // Helper methods to get counts without loading entire collections
    @get:Transient
    @get:JsonIgnore
    val teacherCount: Long
        get() = Teacher.count("user.school", this)

    @get:Transient
    @get:JsonIgnore
    val classCount: Long
        get() = SchoolClass.count("school", this)

    @get:Transient
    @get:JsonIgnore
    val studentCount: Long
        get() = Student.count("user.school", this)

    // New helper methods
    @get:Transient
    @get:JsonIgnore
    val studentCapacityPercentage: Double
        get() = if (studentsCapacity > 0) {
            (studentCount.toDouble() / studentsCapacity) * 100
        } else 0.0

    @get:Transient
    @get:JsonIgnore
    val schoolAge: Int?
        get() = establishedYear?.let { Year.now().value - it }

    @get:Transient
    @get:JsonIgnore
    val isAtCapacity: Boolean
        get() = studentsCapacity in 1..studentCount

    @get:Transient
    @get:JsonIgnore
    val hasAvailableSeats: Boolean
        get() = studentsCapacity == 0 || studentCount < studentsCapacity

    @get:Transient
    @get:JsonIgnore
    val availableSeats: Int
        get() = if (studentsCapacity > 0) {
            max(0, studentsCapacity - studentCount.toInt())
        } else 0

    @get:Transient
    @get:JsonIgnore
    val hasTransportFacilitySafe: Boolean
        get() = hasTransportFacility

    @get:Transient
    @get:JsonIgnore
    val hasHostelFacilitySafe: Boolean
        get() = hasHostelFacility

    @get:Transient
    @get:JsonIgnore
    val hasCafeteriaSafe: Boolean
        get() = hasCafeteria

    @get:Transient
    @get:JsonIgnore
    val hasLibrarySafe: Boolean
        get() = hasLibrary

    @get:Transient
    @get:JsonIgnore
    val hasSportsFacilitySafe: Boolean
        get() = hasSportsFacility

    override fun toString(): String =
        "School(id=$id, name='$name', code='$code', status=$status, type=$schoolType, level=$educationalLevel)"

    companion object : PanacheCompanion<School> {
        fun findActive(): List<School> {
            return find("status", SchoolStatus.ACTIVE).list()
        }

        fun findByCode(code: String): School? {
            return find("code", code).firstResult()
        }

        fun findByType(schoolType: SchoolType): List<School> {
            return find("schoolType", schoolType).list()
        }

        fun findByEducationalLevel(level: EducationalLevel): List<School> {
            return find("educationalLevel", level).list()
        }

        fun findWithAvailableCapacity(): List<School> {
            return list("FROM School s WHERE s.status = ?1 AND s.studentsCapacity > 0 AND s.studentsCapacity > (SELECT COUNT(st) FROM Student st WHERE st.user.school = s)", SchoolStatus.ACTIVE)
        }
    }
}
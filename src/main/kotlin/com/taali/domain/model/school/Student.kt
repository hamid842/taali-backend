package com.taali.domain.model.school

import com.taali.api.dto.school.StudentDTO
import com.taali.api.dto.user.UserDto
import com.taali.domain.enum.Gender
import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "students")
class Student : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: User? = null

    @Column(name = "student_id", unique = true)
    var studentId: String? = null

    @Column(name = "id_number", unique = true)
    var idNumber: String? = null

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: Gender? = null

    @Column(name = "is_active")
    var isActive: Boolean = true

    // Class relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    var schoolClass: SchoolClass? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_parents",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "parent_id")]
    )
    var parents: MutableSet<Parent> = mutableSetOf()

    @Column(name = "emergency_contact")
    var emergencyContact: String? = null

    @Column(name = "emergency_phone")
    var emergencyPhone: String? = null

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    var medicalNotes: String? = null

    // Add grade level
    @Column(name = "grade_level")
    var gradeLevel: String? = null

    // Helper method to get full name from User
    fun getFullName(): String = user?.let { "${it.firstName} ${it.lastName}" } ?: ""

    // Helper method to calculate age
    fun getAge(): Int? = birthDate?.let {
        val now = LocalDate.now()
        var age = now.year - it.year
        if (now < it.plusYears(age.toLong())) age--
        age
    }

    companion object : PanacheCompanion<Student> {
        fun findBySchool(schoolId: Long): List<Student> {
            return find("user.school.id", schoolId).list()
        }

        fun findByUser(userId: Long): Student? {
            return find("user.id", userId).firstResult()
        }

        fun fromEntity(student: Student): StudentDTO {
            return StudentDTO(
                id = student.id, name = student.getFullName(), user = UserDto.fromEntity(student.user)
            )
        }
    }
}
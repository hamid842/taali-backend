package com.taali.domain.model.school

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

    @Column(name = "first_name", nullable = false)
    var firstName: String = ""

    @Column(name = "last_name", nullable = false)
    var lastName: String = ""

    @Column(name = "student_id", unique = true)
    var studentId: String? = null

    @Column(name = "id_number", unique = true)
    var idNumber: String? = null

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: Gender? = null

    // School relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null

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

    // Helper method to get full name
    fun getFullName(): String = "$firstName $lastName"

    // Helper method to calculate age
    fun getAge(): Int? = birthDate?.let {
        val now = LocalDate.now()
        var age = now.year - it.year
        if (now < it.plusYears(age.toLong())) age--
        age
    }

    companion object : PanacheCompanion<Student>
}

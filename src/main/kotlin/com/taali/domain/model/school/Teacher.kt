package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "teachers")
class Teacher : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: User? = null

    @Column(name = "is_active")
    var isActive: Boolean = true

    @ElementCollection
    @CollectionTable(name = "teacher_specializations", joinColumns = [JoinColumn(name = "teacher_id")])
    @Column(name = "specialization")
    var specializations: MutableSet<String> = mutableSetOf()

    @Column(name = "qualification")
    var qualification: String? = null

    @Column(name = "experience_years")
    var experienceYears: Int? = null

    @Column(name = "hire_date")
    var hireDate: java.time.LocalDate? = null

    @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var classAssignments: MutableSet<ClassTeacher> = mutableSetOf()

    companion object : PanacheCompanion<Teacher> {
        fun findBySchool(schoolId: Long): List<Teacher> {
            return find("user.school.id", schoolId).list()
        }

        fun findByUser(userId: Long): Teacher? {
            return find("user.id", userId).firstResult()
        }
    }


    // Helper method to get full name from User
    fun getFullName(): String = user?.let { "${it.firstName} ${it.lastName}" } ?: ""

    // Helper method to get email from User
    fun getEmail(): String? = user?.email

    // Helper method to get phone from User
    fun getPhone(): String? = user?.phoneNumber
}
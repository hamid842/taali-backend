package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "parents")
class Parent : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: User? = null

    @Column(name = "occupation")
    var occupation: String? = null

    @Column(name = "address")
    var address: String? = null

    @Column(name = "is_active")
    var isActive: Boolean = true

    // Fix the relationship - use @ManyToMany with proper join table
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_parents",
        joinColumns = [JoinColumn(name = "parent_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id")]
    )
    var students: MutableSet<Student> = mutableSetOf()

    companion object : PanacheCompanion<Parent> {
        fun findBySchool(schoolId: Long): List<Parent> {
            return find("school.id", schoolId).list()
        }

        fun findByUser(userId: Long): Parent? {
            return find("user.id", userId).firstResult()
        }
    }

    // Helper methods to get data from User
    fun getFullName(): String = user?.let { "${it.firstName} ${it.lastName}" } ?: ""
    fun getPhone(): String? = user?.phoneNumber
    fun getEmail(): String? = user?.email
}
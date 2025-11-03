package com.taali.domain.model.school

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "students")
data class Student(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    var phone: String? = null,

    var birthDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    var classroom: Classroom? = null,

    @ManyToMany
    @JoinTable(
        name = "student_parents",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "parent_id")]
    )
    var parents: MutableList<Parent> = mutableListOf(),

    var isActive: Boolean = true
)

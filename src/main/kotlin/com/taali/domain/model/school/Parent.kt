package com.taali.domain.model.school

import jakarta.persistence.*

@Entity
@Table(name = "parents")
data class Parent(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    var phone: String? = null,

    var occupation: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null,

    @ManyToMany(mappedBy = "parents")
    var students: MutableList<Student> = mutableListOf(),

    var isActive: Boolean = true
)

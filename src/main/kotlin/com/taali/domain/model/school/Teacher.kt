package com.taali.domain.model.school

import jakarta.persistence.*

@Entity
@Table(name = "teachers")
data class Teacher(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    var phone: String? = null,

    var subject: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null,

    @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL], orphanRemoval = true)
    var classrooms: MutableList<Classroom> = mutableListOf(),

    var isActive: Boolean = true
)

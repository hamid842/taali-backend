package com.taali.domain.model

import jakarta.persistence.*;


@Entity
@Table(name = "parents")
data class Parents(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var phoneNumber: String,

    @ManyToMany
    @JoinTable(
        name = "parent_student",
        joinColumns = [JoinColumn(name = "parent_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id")]
    )
    var children: MutableList<Student> = mutableListOf()
)


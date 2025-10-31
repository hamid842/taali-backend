package com.taali.domain.model

import jakarta.persistence.*;


@Entity
@Table(name = "students")
data class Student(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,

    @ManyToMany(mappedBy = "students")
    var classrooms: MutableList<Classroom> = mutableListOf(),

    @ManyToMany(mappedBy = "children")
    var parents: MutableList<Parents> = mutableListOf()
)



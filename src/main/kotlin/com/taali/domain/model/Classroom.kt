package com.taali.domain.model

import jakarta.persistence.*;

@Entity
@Table(name = "classrooms")
data class Classroom(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,

    @ManyToOne @JoinColumn(name = "teacher_id")
    var teacher: Teacher? = null,

    @ManyToOne @JoinColumn(name = "school_id")
    var school: School? = null,

    @ManyToMany
    @JoinTable(
        name = "classroom_student",
        joinColumns = [JoinColumn(name = "classroom_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id")]
    )
    var students: MutableList<Student> = mutableListOf()
)

package com.taali.domain.model

import jakarta.persistence.*;


@Entity
@Table(name = "teachers")
data class Teacher(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var email: String,

    @ManyToOne @JoinColumn(name = "school_id")
    var school: School? = null,

    @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL])
    var classrooms: MutableList<Classroom> = mutableListOf()
)


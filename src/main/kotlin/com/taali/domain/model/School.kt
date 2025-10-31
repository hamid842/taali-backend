package com.taali.domain.model

import jakarta.persistence.*


@Entity
@Table(name = "schools")
data class School(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var address: String,

    @ManyToOne @JoinColumn(name = "owner_id")
    var owner: Owner? = null,

    @OneToMany(mappedBy = "school", cascade = [CascadeType.ALL])
    var admins: MutableList<SchoolAdmin> = mutableListOf(),

    @OneToMany(mappedBy = "school", cascade = [CascadeType.ALL])
    var teachers: MutableList<Teacher> = mutableListOf(),

    @OneToMany(mappedBy = "school", cascade = [CascadeType.ALL])
    var classrooms: MutableList<Classroom> = mutableListOf()
)



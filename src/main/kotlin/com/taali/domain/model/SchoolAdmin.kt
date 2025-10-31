package com.taali.domain.model

import jakarta.persistence.*;


@Entity
@Table(name = "school_admins")
data class SchoolAdmin(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var email: String,

    @ManyToOne @JoinColumn(name = "school_id")
    var school: School? = null
)

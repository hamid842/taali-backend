package com.taali.domain.model

import jakarta.persistence.*;

@Entity
@Table(name = "owners")
data class Owner(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var email: String,

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL])
    var schools: MutableList<School> = mutableListOf()
)



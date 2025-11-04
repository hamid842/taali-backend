package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "canteens")
class Canteen : AuditableEntity() {

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "location")
    var location: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", unique = true)
    var operator: User? = null

    companion object : PanacheCompanion<Canteen>
}

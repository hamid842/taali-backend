package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "teachers")
class Teacher : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: User? = null

    @Column(name = "first_name", nullable = false)
    var firstName: String = ""

    @Column(name = "last_name", nullable = false)
    var lastName: String = ""

    @Column(name = "email")
    var email: String? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "subject_specialization")
    var specialization: String? = null

    @Column(name = "qualification")
    var qualification: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null

    companion object : PanacheCompanion<Teacher>

}
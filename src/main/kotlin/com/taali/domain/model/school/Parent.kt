package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import jakarta.persistence.*

@Entity
@Table(name = "parents")
class Parent : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: User? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "occupation")
    var occupation: String? = null

    @Column(name = "address")
    var address: String? = null

    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    var students: MutableSet<Student> = mutableSetOf()
}

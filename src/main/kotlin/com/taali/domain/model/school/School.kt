package com.taali.domain.model.school

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taali.domain.enum.SchoolStatus
import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.menu.MenuItem
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "schools")
class School : AuditableEntity() {

    @Column(name = "name", nullable = false, length = 255)
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    var name: String = ""

    @Column(name = "code", unique = true, nullable = false, length = 50)
    @field:NotBlank(message = "School code is required")
    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    var code: String = ""

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "address", columnDefinition = "TEXT")
    var address: String? = null

    @Column(name = "email", length = 255)
    @field:Email(message = "Please provide a valid email address")
    var email: String? = null

    @Column(name = "phone", length = 20)
    var phone: String? = null

    @Column(name = "owner_id")
    var ownerId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SchoolStatus = SchoolStatus.ACTIVE


    // Helper methods to get counts without loading entire collections
    @get:Transient
    @get:JsonIgnore
    val teacherCount: Long
        get() = Teacher.count("user.school", this)

    @get:Transient
    @get:JsonIgnore
    val classCount: Long
        get() = SchoolClass.count("school", this)

    @get:Transient
    @get:JsonIgnore
    val studentCount: Long
        get() = Student.count("user.school", this)


    override fun toString(): String =
        "School(id=$id, name='$name', code='$code', status=$status)"

    companion object : PanacheCompanion<School> {
        fun findActive(): List<School> {
            return find("isActive", true).list()
        }

        fun findByCode(code: String): School? {
            return find("code", code).firstResult()
        }
    }

}


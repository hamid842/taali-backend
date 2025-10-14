package com.taali.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "users")
class User : PanacheEntity() {

    @Column(unique = true, nullable = false)
    @field:NotBlank(message = "validation.email.required")
    @field:Email(message = "validation.email.invalid")
    var email: String = ""

    @Column(nullable = false)
    @field:NotBlank(message = "validation.password.required")
    @field:Size(min = 6, message = "validation.password.length")
    var password: String = ""

    @Column(name = "first_name", nullable = false)
    @field:NotBlank(message = "validation.firstname.required")
    var firstName: String = ""

    @Column(name = "last_name", nullable = false)
    @field:NotBlank(message = "validation.lastname.required")
    var lastName: String = ""

    @Column(name = "phone", nullable = false)
    @field:NotBlank(message = "validation.phone.required")
    var phone: String = ""

    @Column(nullable = false)
    @field:NotBlank(message = "validation.role.required")
    var role: String = "STUDENT" // STUDENT, TEACHER, ADMIN, PARENT

    @Column(name = "is_active")
    var isActive: Boolean = true

    @Column(name = "email_verified")
    var emailVerified: Boolean = false

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "last_login")
    var lastLogin: LocalDateTime? = null

    companion object {
        fun findByEmail(email: String): User? {
            return find("email", email).firstResult()
        }

        fun existsByEmail(email: String): Boolean {
            return find("email", email).count() > 0
        }

        fun findActiveByEmail(email: String): User? {
            return find("email = ?1 and isActive = true", email).firstResult()
        }
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
package com.taali.entity

import com.taali.enum.UserRole
import com.taali.enum.UserStatus
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
@Table(name = "users")
class User : PanacheEntity() {

    @Column(nullable = false) var firstName: String = ""

    @Column(nullable = false) var lastName: String = ""

    @Column(unique = true, nullable = false) var email: String = ""

    @Column(nullable = false) var phoneNumber: String = ""

    @Column(nullable = false) var passwordHash: String = ""

    @Enumerated(EnumType.STRING) @Column(nullable = false) var role: UserRole = UserRole.STUDENT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.PENDING

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object : PanacheCompanion<User> {
        fun findByEmail(email: String): User? {
            return find("email", email).firstResult()
        }

        fun existsByEmail(email: String): Boolean {
            return count("email", email) > 0
        }

        fun findByPhoneNumber(phoneNumber: String): User? {
            return find("phoneNumber", phoneNumber).firstResult()
        }
    }
}

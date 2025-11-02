package com.taali.domain.model.school

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "schools")
class School : PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schools_seq")
    @SequenceGenerator(name = "schools_seq", sequenceName = "schools_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "name", nullable = false)
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    var name: String = ""

    @Column(name = "code", unique = true, nullable = false)
    @field:NotBlank(message = "School code is required")
    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    var code: String = ""

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "address", columnDefinition = "TEXT")
    var address: String? = null

    @Column(name = "email")
    @field:Email(message = "Please provide a valid email address")
    var email: String? = null

    @Column(name = "phone", length = 20)
    var phone: String? = null

    @Column(name = "owner_id")
    var ownerId: String? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
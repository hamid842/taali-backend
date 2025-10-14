package com.taali.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken : PanacheEntity() {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @Column(unique = true, nullable = false)
    lateinit var token: String

    @Column(name = "expires_at", nullable = false)
    lateinit var expiresAt: LocalDateTime

    @Column(name = "is_revoked")
    var isRevoked: Boolean = false

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun findByToken(token: String): RefreshToken? {
            return find("token = ?1 and isRevoked = false and expiresAt > ?2",
                token, LocalDateTime.now()).firstResult()
        }
    }

    fun revoke() {
        this.isRevoked = true
        this.persist()
    }
}
package com.taali.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken : PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @Column(unique = true, nullable = false, length = 500) lateinit var token: String

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "is_revoked", nullable = false) var isRevoked: Boolean = false

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at") var updatedAt: LocalDateTime? = null

    companion object : PanacheCompanion<RefreshToken> {
        fun findByToken(token: String): RefreshToken? {
            return find(
                            "token = ?1 and isRevoked = false and expiresAt > ?2",
                            token,
                            LocalDateTime.now()
                    )
                    .firstResult()
        }

        fun findByUser(user: User): List<RefreshToken> {
            return find(
                            "user = ?1 and isRevoked = false and expiresAt > ?2",
                            user,
                            LocalDateTime.now()
                    )
                    .list()
        }

        @Transactional
        fun deleteExpiredTokens() {
            delete("expiresAt < ?1 or isRevoked = true", LocalDateTime.now())
        }

        @Transactional
        fun revokeAllUserTokens(user: User) {
            update(
                    "isRevoked = true, updatedAt = ?1 where user = ?2 and isRevoked = false",
                    LocalDateTime.now(),
                    user
            )
        }
    }

    @Transactional
    fun revoke() {
        this.isRevoked = true
        this.updatedAt = LocalDateTime.now()
        this.persist()
    }

    fun isValid(): Boolean {
        return !isRevoked && expiresAt.isAfter(LocalDateTime.now())
    }

    @PreUpdate
    fun onUpdate() {
        this.updatedAt = LocalDateTime.now()
    }
}

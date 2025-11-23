package com.taali.domain.model.notification

import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.user.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "device_tokens")
class DeviceToken : AuditableEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @Column(name = "expo_push_token", nullable = false, unique = true)
    lateinit var expoPushToken: String

    @Column(name = "device_type")
    var deviceType: String? = null // "ios", "android", "web"

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    companion object : PanacheCompanion<DeviceToken> {
        fun findByUser(userId: Long): List<DeviceToken> {
            return list("user.id = ?1 AND isActive = true", userId)
        }

        fun findByExpoToken(token: String): DeviceToken? {
            return find("expoPushToken = ?1 AND isActive = true", token).firstResult()
        }

        fun deactivateUserTokens(userId: Long) {
            update("isActive = false WHERE user.id = ?1", userId)
        }
    }
}
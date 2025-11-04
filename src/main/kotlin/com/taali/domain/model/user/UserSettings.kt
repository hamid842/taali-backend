package com.taali.domain.model.user

import com.taali.domain.model.common.AuditableEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_settings")
class UserSettings : AuditableEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    lateinit var user: User

    @Column(name = "preferred_language", nullable = false)
    var preferredLanguage: String = "en"

    @Column(name = "font_size", nullable = false)
    var fontSize: String = "medium" // small, medium, large

    @Column(name = "theme", nullable = false)
    var theme: String = "light" // light, dark, system

    @Column(name = "primary_color", nullable = false)
    var primaryColor: String = "blue" // blue, green, purple, etc.

    @Column(name = "notifications_enabled", nullable = false)
    var notificationsEnabled: Boolean = true

    @Column(name = "email_notifications", nullable = false)
    var emailNotifications: Boolean = true

    @Column(name = "sms_notifications", nullable = false)
    var smsNotifications: Boolean = false


    companion object {
        fun createDefault(user: User): UserSettings {
            return UserSettings().apply {
                this.user = user
            }
        }
    }
}

package com.taali.infrastructure.persistence.repository.user

import com.taali.domain.model.user.User
import com.taali.domain.model.user.UserSettings
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.Locale

@ApplicationScoped
class UserSettingsRepository : PanacheRepository<UserSettings> {

    fun findByUserId(userId: Long): UserSettings? {
        return find("user.id", userId).firstResult()
    }

    fun findByUserEmail(email: String): UserSettings? {
        return find("user.email", email).firstResult()
    }

    @Transactional
    fun findOrCreateDefault(user: User): UserSettings {
        return findByUserId(user.id!!) ?: UserSettings.createDefault(user).also {
            persist(it)
        }
    }

    fun existsByUserId(userId: Long): Boolean {
        return count("user.id", userId) > 0
    }

    @Transactional
    fun deleteByUserId(userId: Long): Boolean {
        return delete("user.id", userId) > 0
    }

    @Transactional
    fun updatePreferredLanguage(userId: Long, language: Locale): UserSettings? {
        val settings = findByUserId(userId)
        settings?.let {
            it.preferredLanguage = language
            persist(it)
        }
        return settings
    }

    @Transactional
    fun updateTheme(userId: Long, theme: String): UserSettings? {
        val settings = findByUserId(userId)
        settings?.let {
            it.theme = theme
            persist(it)
        }
        return settings
    }

    fun findByTheme(theme: String): List<UserSettings> {
        return list("theme", theme)
    }

    fun findByLanguage(language: String): List<UserSettings> {
        return list("preferredLanguage", language)
    }

    fun findUsersWithEmailNotifications(): List<UserSettings> {
        return list("emailNotifications", true)
    }

    fun findUsersWithSmsNotifications(): List<UserSettings> {
        return list("smsNotifications", true)
    }

    fun findUsersWithNotificationsEnabled(): List<UserSettings> {
        return list("notificationsEnabled", true)
    }
}
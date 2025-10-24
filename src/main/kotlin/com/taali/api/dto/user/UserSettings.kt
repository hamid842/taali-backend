package com.taali.api.dto.user

import java.time.LocalDateTime
import java.util.Locale

data class UserSettingsDto(
    val id: Long? = null,
    val preferredLanguage: Locale,
    val fontSize: String,
    val theme: String,
    val primaryColor: String,
    val notificationsEnabled: Boolean,
    val emailNotifications: Boolean,
    val smsNotifications: Boolean,
    val updatedAt: LocalDateTime? = null
)

data class UpdateUserSettingsRequest(
    val preferredLanguage: Locale? = null,
    val fontSize: String? = null,
    val theme: String? = null,
    val primaryColor: String? = null,
    val notificationsEnabled: Boolean? = null,
    val emailNotifications: Boolean? = null,
    val smsNotifications: Boolean? = null
)